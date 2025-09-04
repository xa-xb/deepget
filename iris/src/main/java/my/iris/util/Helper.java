package my.iris.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.servlet.http.HttpServletRequest;
import my.iris.cache.SystemCache;
import my.iris.config.AppConfig;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * helper class
 */
public record Helper() {

    // default date formatter
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(AppConfig.DATE_TIME_FORMAT);
    public static final DateTimeFormatter DATE_TIME_FORMATTER_DATE = DateTimeFormatter.ofPattern(AppConfig.DATE_FORMAT);
    public static final DateTimeFormatter DATE_TIME_FORMATTER_TIME = DateTimeFormatter.ofPattern(AppConfig.TIME_FORMAT);

    private static final DecimalFormat decimalFormat = new DecimalFormat("0.00");

    private static SystemCache systemCache = null;


    // 移动端判别条件
    private final static String[] MOBILE_USER_AGENT_KEY_WORDS = {" (iPhone; CPU ", " (iPad; CPU ", " Android "};

    private static SystemCache getSystemCache() {
        if (systemCache == null) {
            systemCache = AppConfig.getContext().getBean(SystemCache.class);
        }
        return systemCache;
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (Byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * 格式化日期 'yyyy-MM-dd HH:mm:ss'
     *
     * @param temporal date
     * @return str
     */
    public static String dateFormat(TemporalAccessor temporal) {

        return temporal == null ? null : DATE_TIME_FORMATTER.format(temporal);
    }

    public static String dateFormat(LocalDateTime localDateTime, String pattern) {
        return localDateTime == null || pattern == null ? null : DateTimeFormatter.ofPattern(pattern).format(localDateTime);
    }

    /**
     * get client ip from http request
     *
     * @param request client request
     * @return client ip
     */
    public static String getClientIp(HttpServletRequest request) {
        var key = getSystemCache().getRealIpHeader();
        if (StringUtils.hasText(key)) {
            var ip = request.getHeader(key);
            if (StringUtils.hasText(ip)) return ip;
        }
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor == null || xForwardedFor.length() < 6) {
            return request.getRemoteAddr();
        }
        return xForwardedFor.split(",")[0].trim();
    }


    /**
     * 生成二维码
     *
     * @param text  需要生成二维码的内容
     * @param width 二维码宽高度
     * @return 二维码字节
     */
    public static byte[] getQRCodePng(String text, int width) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            //noinspection SuspiciousNameCombination
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, width);
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            return pngOutputStream.toByteArray();
        } catch (Exception ex) {
            LogUtils.error(Helper.class, "", ex);
            return null;
        }
    }

    /**
     * return size str, like this: 1024 -> 1KB
     *
     * @param size size
     * @return size str
     */
    public static String getSizeHumanReadable(long size) {
        String numStr, unit;
        BigDecimal num = new BigDecimal(size);
        if (size < 1024) {
            return num.toString();
        }
        if (size < 0x10_0000) {
            numStr = num.divide(new BigDecimal(1024), 2, RoundingMode.HALF_UP).toString();
            unit = "Ki";
        } else if (size < 0x400_00000) {
            numStr = num.divide(new BigDecimal(0x100000), 2, RoundingMode.HALF_UP).toString();
            unit = "Mi";
        } else if (size < 0x100_0000_0000L) {
            numStr = num.divide(new BigDecimal(0x400_00000), 2, RoundingMode.HALF_UP).toString();
            unit = "Gi";
        } else {
            numStr = num.divide(new BigDecimal(0x100_0000_0000L), 2, RoundingMode.HALF_UP).toString();
            unit = "Pi";
        }
        if (numStr.endsWith("00")) {
            numStr = numStr.substring(0, numStr.length() - 3);
        } else if (numStr.endsWith("0")) {
            numStr = numStr.substring(0, numStr.length() - 1);
        }
        return numStr + unit + "B";

    }


    /***
     * is it mobile request
     * @param request http request
     * @return mobile request
     */
    public static boolean isMobileRequest(HttpServletRequest request) {
        String userAgent = request.getHeader("user-agent");
        if (StringUtils.hasLength(userAgent)) {
            for (var keyWord : MOBILE_USER_AGENT_KEY_WORDS) {
                if (userAgent.contains(keyWord)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 返回当前时间, yyyy-MM-dd HH:mm:ss
     *
     * @return 当前时间
     */
    public static String now() {
        return dateFormat(LocalDateTime.now());
    }


    /**
     * 从字符串中解析日期，需要符合"yyyy-MM-dd HH:mm:ss"格式
     *
     * @param str 待解析的字符串
     * @return 日期，失败返回null
     */
    public static LocalDateTime parseLocalDateTime(String str) {
        try {
            return LocalDateTime.parse(str, DATE_TIME_FORMATTER);
        } catch (Exception ignored) {
            return null;
        }
    }

    public static <T extends Number> T parseNumber(Object obj, Class<T> targetClass) {
        return parseNumber(obj, targetClass, "0");
    }

    public static <T extends Number> T parseNumber(Object obj, Class<T> targetClass, String defaultValue) {
        if (obj == null) {
            obj = defaultValue;
        }
        T result;
        try {
            result = NumberUtils.parseNumber(obj.toString(), targetClass);
        } catch (Exception ignored) {
            if (defaultValue == null) {
                return null;
            }
            result = NumberUtils.parseNumber(defaultValue, targetClass);
        }
        return result;
    }

    public static <T extends Number> List<T> parseNumberList(String str, Class<T> targetClass) {
        return parseNumberList(str, targetClass, ",");
    }

    public static <T extends Number> List<T> parseNumberList(String str, Class<T> targetClass, String separator) {
        List<T> list = new LinkedList<>();
        if (str == null) {
            return list;
        }
        String[] parts = str.replaceAll("\\s", "").split(separator);
        for (String part : parts) {
            T num = parseNumber(part, targetClass, null);
            if (num != null) {
                list.add(num);
            }
        }
        return list;
    }

    /**
     * 格式化价格
     *
     * @param bigDecimal 价格
     * @return 价格(元, 保留两位小数)
     */
    public static String priceFormat(BigDecimal bigDecimal) {
        return decimalFormat.format(bigDecimal);
    }

    /**
     * 价格由小数点的元单位变为长整型的分单位
     *
     * @param str 要转换的价格(元)
     * @return 价格(分 ）
     */
    public static long priceToLong(String str) {
        BigDecimal bigDecimal = new BigDecimal(str);
        bigDecimal = bigDecimal.multiply(new BigDecimal(100)).setScale(0, RoundingMode.UNNECESSARY);
        return Long.parseLong(bigDecimal.toString());
    }

    /**
     * Generates a random numeric string of the specified length.
     *
     * @param length the desired number of digits; if ≤ 0, returns an empty string
     * @return a string of random digits [0-9]
     */
    public static String randomNumber(int length) {
        if (length <= 0) return "";
        StringBuilder sb = new StringBuilder(length);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10)); // 0–9
        }
        return sb.toString();
    }

    /**
     * Generates a random lowercase alphabetic string.
     *
     * @param length the desired length of the string; if {@code len <= 0}, an empty string is returned
     * @return a random string consisting of lowercase letters [a-z]
     */
    public static String randomString(int length) {
        if (length <= 0) return "";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append((char) (ThreadLocalRandom.current().nextInt(26) + 'a'));
        }
        return sb.toString();
    }

    /**
     * Renders a Thymeleaf template with the given variables.
     *
     * @param template  the name of the template file (without suffix)
     * @param variables the variables to inject into the template; may be {@code null}
     * @return the rendered HTML/text as a {@code String}
     */

    public static String renderTemplate(String template, Map<String, Object> variables) {
        var systemCache = AppConfig.getContext().getBean(SystemCache.class);
        Map<String, Object> map = new HashMap<>();
        map.put("siteName", systemCache.getSiteName());
        map.put("siteUrl", systemCache.getSiteUrl());
        var templateEngine = AppConfig.getContext().getBean(TemplateEngine.class);
        var context = new Context();
        if (variables != null && !variables.isEmpty()) {
            map.putAll(variables);
        }
        context.setVariables(map);
        return templateEngine.process(template, context);
    }

    /***
     * replaceAll,忽略大小写
     *
     * @param input 输入字符串
     * @param regex 待替换的内容(正则)
     * @param replacement 要替换的字符串
     * @return 替换后的字符串
     */
    public static String stringReplaceAll(String input, String regex, String replacement) {
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(input);
        return m.replaceAll(replacement);

    }

    /**
     * url编码
     *
     * @param str 要编码的字符串
     * @return url编码后的字符串
     */
    public static String urlDecode(String str) {
        return URLDecoder.decode(str, StandardCharsets.UTF_8);
    }

    /**
     * url解码
     *
     * @param str 要解码的字符串
     * @return url解码后的字符串
     */
    public static String urlEncode(String str) {
        return URLEncoder.encode(str, StandardCharsets.UTF_8);
    }

}
