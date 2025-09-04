package my.iris.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties(AppProperties.class)
public class AppConfig {
    public final static String DATE_FORMAT = "yyyy-MM-dd";
    public final static String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public final static String TIME_FORMAT = "HH:mm:ss";
    public final static String TIME_ZONE = "GMT+8";

    // application dir
    @Getter
    private static String appDir;

    @Getter
    private static ConfigurableApplicationContext context;

    @Autowired
    private void setConfigurableApplicationContext(ConfigurableApplicationContext context) {
        appDir = System.getProperty("user.dir");
        AppConfig.context = context;
    }


}
