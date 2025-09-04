package my.iris;

import my.iris.config.AppConfig;
import my.iris.util.Helper;
import my.iris.util.LogUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;


@DependsOn({"appConfig", "jsonUtils", "securityUtils"})
@EnableAsync
@EnableScheduling
@SpringBootApplication
public class Launcher {
    public static void main(String[] args) {
        // 设置应用程序的基础配置
        configureApplicationDefaults();

        // 启动 Spring 应用
        SpringApplication application = new SpringApplication(Launcher.class);
        application.run(args);

        // 自访问预热
        try {
            warmUpApplication();
        } catch (Exception e) {
            LogUtils.warn(Launcher.class, "Application warm-up failed", e);
        }
    }

    private static void configureApplicationDefaults() {
        Locale.setDefault(Locale.CHINA);
        TimeZone.setDefault(TimeZone.getTimeZone(AppConfig.TIME_ZONE));
    }

    /**
     * 通过自我调用 访问.. 等方式热机
     */
    private static void warmUpApplication() {
        Helper.getQRCodePng("https://hot.test", 100);
        ConfigurableEnvironment environment = AppConfig.getContext().getEnvironment();
        String addr = environment.getProperty("server.address");
        if (addr == null || addr.startsWith("0.")) {
            addr = "localhost";
        }
        int port = Helper.parseNumber(environment.getProperty("server.port"), Integer.class);
        if (port == 0) {
            port = 8080;
        }
        Set<String> links = new LinkedHashSet<>();
        links.add("/api/v1/main/captcha");
        for (String link : links) {
            sendRequest(String.format("http://%s:%d%s", addr, port, link));
        }
    }

    private static void sendRequest(String uri) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .GET()
                .build();
        try (var client = HttpClient.newHttpClient()) {
            var response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() != HttpStatus.OK.value()) {
                LogUtils.warn(Launcher.class, uri + " " + response.statusCode());
            }
        } catch (IOException | InterruptedException ex) {
            LogUtils.warn(Launcher.class, ex);
        }
    }
}
