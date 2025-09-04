package my.iris.util;

import jakarta.validation.Validator;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

@Component
public final class ValidatorUtils {

    public static final String ACCOUNT_NOT_EXIST = "账号不存在";
    public static final String ACCOUNT_OR_PWD_ERROR = "账号或密码错误";

    private static final Set<String> ALLOWED_SVG_TAGS = Set.of(
            "defs", "circle", "ellipse", "g", "line", "lineargradient", "path", "polygon", "polyline",
            "radialgradient", "rect", "stop", "style", "svg", "title", "text"
    );
    public static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();

    private static Validator validator;

    public ValidatorUtils(Validator validator) {
        ValidatorUtils.validator = validator;
    }

    public static String validate(Object obj, Class<?>... classes) {
        var violation = validator.validate(obj, classes);
        if (violation.isEmpty()) {
            return null;
        }
        var hint = violation.iterator().next();
        return "%s %s".formatted(hint.getPropertyPath().toString(), hint.getMessage());
    }

    public static String validateCaptcha(String captcha) {
        return validateCaptcha(captcha, "验证码");
    }

    /**
     * validate captcha
     *
     * @param captcha captcha
     * @param alias   alias
     * @return null: success or error msg
     */
    public static String validateCaptcha(String captcha, String alias) {
        var msg = notEmpty(captcha, alias);
        if (msg != null) {
            return msg;
        }
        var code = TaskContext.getSession().get(CaptchaUtils.CAPTCHA_SESSION_NAME);
        if (captcha.equals(code)) {
            return null;
        }
        return alias + "不正确";
    }

    public static String validateEmail(String data) {
        return validateEmail(data, "邮箱");
    }

    /**
     * validate email address
     *
     * @param data  email address to validate
     * @param alias field alias for error message
     * @return null if valid, or error message
     */
    public static String validateEmail(String data, String alias) {
        // RFC 5322 标准的邮箱正则表达式
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";
        if (!Pattern.matches(emailRegex, data)) {
            return alias + "格式不正确";
        }
        return null;
    }


    public static String validateMobile(String data) {
        return validateMobile(data, "手机号码");
    }

    /**
     * validate mobile phone
     *
     * @param data  手机号码
     * @param alias 手机号码别称
     * @return null 成功， or 错误信息
     */
    public static String validateMobile(String data, String alias) {
        if (data == null || data.isEmpty()) {
            return null;
        }
        if (!Pattern.matches("^1\\d{10}$", data)) {
            return alias + "不正确";
        }
        return null;
    }


    public static String validateUserName(String data) {
        return validateUserName(data, "用户名");
    }

    /**
     * 验证用户名
     *
     * @param data  要判断的用户名
     * @param alias 用户名别称
     * @return null 成功， or 错误信息
     */
    public static String validateUserName(String data, String alias) {
        String msg = notEmpty(data, alias);
        if (msg != null) {
            return msg;
        }
        if (data.length() < 3) {
            return alias + "不能小于3位";
        }
        if (data.length() > 16) {
            return alias + "不能超过16位";
        }
        if (!Pattern.matches("^[a-z].+", data)) {
            return alias + "必须字母开头";
        }
        if (Pattern.matches("^\\d+$", data)) {
            return alias + "不能是纯数字";
        }
        if (!Pattern.matches("^[a-z\\d]+$", data)) {
            return alias + "只能由字母或数字组成";
        }
        if (data.toLowerCase().startsWith("admin")) {
            return alias + "不能以 admin 开头";
        }
        return null;
    }

    public static boolean validateNotNameAndPassword(String name, String password) {
        return !(validateUserName(name, "") == null && validatePassword(password, "") == null);
    }

    /**
     * 不为空判断
     *
     * @param data  要判断的内容,内容不能为null,或全是空格
     * @param alias 别名
     * @return null 成功， or 错误信息
     */
    public static String notEmpty(String data, String alias) {
        if (data == null || data.trim().isEmpty()) {
            return alias + "不能为空";
        }

        return null;
    }

    public static String validatePassword(String data) {
        return validatePassword(data, "密码");
    }

    public static String validatePassword(String data, String alias) {
        String msg = notEmpty(data, alias);
        if (msg != null) {
            return msg;
        }
        if (data.length() < 6) {
            return alias + "不能少与6位";
        }
        if (data.length() > 32) {
            return alias + "不能超过32位";
        }
        return null;
    }

    public static String validatePrice(String data, String alias) {
        String msg = notEmpty(data, alias);
        if (msg != null) {
            return msg;
        }
        data = data.trim();
        if (Pattern.matches("^\\d+$", data)) {
            return null;
        }
        if (Pattern.matches("^\\d+\\.\\d{0,2}$", data)) {
            return null;
        }
        return alias + "格式不正确";
    }

    public static String validateSvg(String data) {
        return validateSvg(data, "SVG");
    }

    public static String validateSvg(String data, String alias) {
        if (data == null || data.isEmpty()) {
            return null;
        }
        DocumentBuilder builder;
        Document doc;
        try {
            builder = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
            doc = builder.parse(new ByteArrayInputStream(data.getBytes()));
        } catch (ParserConfigurationException e) {
            LogUtils.error(ValidatorUtils.class, e);
            return "SVG解析器初始化失败";
        } catch (SAXException | IOException e) {
            return alias + "格式不正确";
        }
        // 校验标签是否全部在白名单内
        var allTags = doc.getElementsByTagName("*");
        for (int i = 0; i < allTags.getLength(); i++) {
            String tagName = allTags.item(i).getNodeName().toLowerCase();
            if (!ALLOWED_SVG_TAGS.contains(tagName)) {
                return alias + "中包含非法标签: " + tagName;
            }
        }
        return null;
    }
}
