package my.iris.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
        prefix = "app"
)
@Data
public final class AppProperties {
    /**
     * AES密钥，用于加/解密数据,请勿随意更改
     */
    private String key;
    /*
     * AES密钥轮换策略新密钥，用于替换旧密钥;
     * 替换任务会在启动后自动执行，完成后日志会输出'StartupTask: aes key replaced successfully'，之后请删除此项配置
     */
    private String newKey;
}
