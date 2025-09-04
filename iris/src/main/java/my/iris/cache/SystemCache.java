package my.iris.cache;

import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.Getter;
import my.iris.model.system.dto.SmtpServerDto;
import my.iris.repository.system.SystemRepository;
import my.iris.util.Helper;
import my.iris.util.JsonUtils;
import my.iris.util.SecurityUtils;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@Getter
public class SystemCache {
    // ICP number
    private String icpNumber;
    // 前端路径
    private String frontendPath;
    // 每日单个邮件地址最大发送数量
    private Integer dailyEmailLimit;

    // 每日单个手机号码最大短信发送数量
    private Integer dailySmsLimit;

    private String rsaPrivateKey;
    private String rsaPublicKey;
    private String realIpHeader;

    //site name
    private String siteName;

    //site url
    private String siteUrl;

    //smtp servers
    private List<SmtpServerDto> smtpServers;

    @Getter(AccessLevel.NONE)
    private final SystemRepository systemRepository;

    public SystemCache(SystemRepository systemRepository) {
        this.systemRepository = systemRepository;
    }


    @PostConstruct
    public void update() {
        systemRepository.findAll().forEach(entity -> {
            if ("sys".equals(entity.getEntity())) {
                switch (entity.getAttribute()) {
                    case "icp_number" -> icpNumber = entity.getValue();
                    case "daily_email_limit" -> dailyEmailLimit = Helper.parseNumber(entity.getValue(), Integer.class);
                    case "daily_sms_limit" -> dailySmsLimit = Helper.parseNumber(entity.getValue(), Integer.class);
                    case "real_ip_header" -> realIpHeader = entity.getValue();
                    case "rsa_private_key" -> rsaPrivateKey = SecurityUtils.aesDecrypt(entity.getValue());
                    case "rsa_public_key" -> rsaPublicKey = entity.getValue();
                    case "site_name" -> siteName = entity.getValue();
                    case "site_url" -> siteUrl = entity.getValue();
                }
                return;
            }
            if ("mail".equals(entity.getEntity())) {
                if ("smtp_servers".equals(entity.getAttribute())) {
                    var tmp = SecurityUtils.aesDecrypt(entity.getValue());
                    smtpServers = JsonUtils.parseList(tmp, SmtpServerDto.class, true);
                }
                return;
            }
            if ("devel".equals(entity.getEntity())) {
                if ("frontend_path".equals(entity.getAttribute())) {
                    frontendPath = entity.getValue();
                }
            }
        });
    }
}
