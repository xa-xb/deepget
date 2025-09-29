package my.iris.task;

import my.iris.cache.SystemCache;
import my.iris.config.AppProperties;
import my.iris.repository.ai.AiProviderRepository;
import my.iris.repository.system.SystemRepository;
import my.iris.service.system.SystemService;
import my.iris.util.LogUtils;
import my.iris.util.SecurityUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class StartupTask {
    private final SystemRepository systemRepository;
    private final SystemCache systemCache;
    AppProperties appProperties;
    SystemService systemService;
    AiProviderRepository aiProviderRepository;

    public StartupTask(AppProperties appProperties, SystemService systemService, SystemRepository systemRepository, SystemCache systemCache) {
        this.appProperties = appProperties;
        this.systemService = systemService;
        this.systemRepository = systemRepository;
        this.systemCache = systemCache;
    }

    @PostConstruct
    public void replaceAesKey() {
        if (
                StringUtils.hasText(appProperties.getNewKey())
                && StringUtils.hasText(appProperties.getKey())
                && !appProperties.getNewKey().equals(appProperties.getKey())
        ) {
            // t_system
            systemService.saveConfig(systemService.getConfig());
            systemRepository.updateValueByEa(systemCache.getRsaPrivateKey(), "sys", "rsa_private_key");
            // t_ai_org
            aiProviderRepository.findAll().forEach(aiOrg -> {
                var key = SecurityUtils.aesDecrypt(aiOrg.getApiKey());
                aiOrg.setApiKey(SecurityUtils.aesEncrypt(key));
            });
            aiProviderRepository.flush();

            LogUtils.info(getClass(), "aes key replaced successfully");
        }
    }
}
