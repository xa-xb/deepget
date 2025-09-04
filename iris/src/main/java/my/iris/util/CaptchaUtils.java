package my.iris.util;


import jakarta.servlet.http.HttpServletResponse;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * 验证码
 */
public record CaptchaUtils() {
    public final static String CAPTCHA_SESSION_NAME = "captcha_code";
    public final static String CAPTCHA_TOKEN_SESSION_NAME = "captcha_token";
    // 验证码长度
    public final static int SIZE = 4;
    private static final char[] mapTable = {
            '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', '0', '1',
            '2', '3', '4', '5', '6', '7',
            '8', '9'};

    /**
     * 删除会话中的验证码
     */
    public static void clear() {
        TaskContext.getSession().remove(CAPTCHA_SESSION_NAME, CAPTCHA_TOKEN_SESSION_NAME);
    }

    /**
     * 获取验证码(size: 90px * 32px)
     *
     * @return 验证码
     */
    public static BufferedImage generateImage(String code) {
        int width = SIZE * 18, height = 32;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // 获取图形上下文
        Graphics g = image.getGraphics();
        //生成随机类
        Random random = new Random();
        // 矩形
        g.fillRect(0, 0, width, height);

        //设定字体
        g.setFont(new Font(null, Font.PLAIN, 24));

        for (int i = 0; i < code.length(); i++) {
            g.setColor(getRandomColor());
            // 数字写入图片
            g.drawString(code.substring(i, i + 1), 16 * i + 6, 25);
        }
        // 干扰线
        for (int i = 0; i < 10; i++) {
            // 设置随机颜色
            g.setColor(getRandomColor());
            // 随机画线
            g.drawLine(random.nextInt(width), random.nextInt(height), random.nextInt(width), random.nextInt(height));
        }
        // 释放图形上下文
        g.dispose();
        return image;
    }

    /**
     * 获得随机颜色
     *
     * @return Color
     */
    static Color getRandomColor() {
        Random ran = new Random();
        return new Color(40 + ran.nextInt(180), 40 + ran.nextInt(180), 40 + ran.nextInt(180));
    }

    /**
     * generate captcha code
     *
     * @return captcha code
     */
    public static String generateCode() {
        StringBuilder phrase = new StringBuilder();
        for (int i = 0; i < CaptchaUtils.SIZE; ++i) {
            phrase.append(mapTable[(int) (mapTable.length * Math.random())]);
        }
        return phrase.toString();
    }

    public static void writeResponse(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store");
        response.setContentType("image/png");
        var code = generateCode();
        TaskContext.getSession().set(CAPTCHA_SESSION_NAME, code);
        try {
            ImageIO.write(generateImage(code), "png", response.getOutputStream());
        } catch (Exception e) {
            LogUtils.error(CaptchaUtils.class, e);
        }
    }
}