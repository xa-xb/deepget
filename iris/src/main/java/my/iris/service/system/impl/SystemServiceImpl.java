package my.iris.service.system.impl;

import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import my.iris.cache.SystemCache;
import my.iris.config.AppConfig;
import my.iris.config.EmailSenderManager;
import my.iris.config.RedisConfig;
import my.iris.model.ApiResult;
import my.iris.model.RsaPubKeyVo;
import my.iris.model.system.dto.ConfigDto;
import my.iris.model.system.vo.SystemInfoVo;
import my.iris.repository.system.SystemRepository;
import my.iris.service.system.AdminLogService;
import my.iris.service.system.SystemService;
import my.iris.util.Helper;
import my.iris.util.JsonUtils;
import my.iris.util.RsaUtils;
import my.iris.util.SecurityUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

@Service
@Transactional
public class SystemServiceImpl implements SystemService {

    @Resource
    AdminLogService adminLogService;

    @Resource
    SystemRepository systemRepository;

    @Resource
    SystemCache systemCache;

    /**
     * 获取系统运行时间
     *
     * @param upTime milliseconds
     * @return String
     */
    private static String getUpTimeStr(long upTime) {
        long days = TimeUnit.MILLISECONDS.toDays(upTime);
        upTime -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(upTime);
        upTime -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(upTime);
        upTime -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(upTime);
        String upTimeStr = "";
        if (days == 1) {
            upTimeStr = "1 day, ";
        } else if (days > 1) {
            upTimeStr = String.format("%d days, ", days);
        }
        upTimeStr += String.format("%02d:%02d:%02d", hours, minutes, seconds);
        return upTimeStr;
    }

    @Override
    public String getBuildTime() {
        try {
            Enumeration<URL> resources = getClass().getClassLoader().getResources("META-INF/MANIFEST.MF");
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                try (InputStream is = url.openStream()) {
                    if (is == null) {
                        continue;
                    }
                    Manifest manifest = new Manifest(is);
                    Attributes attributes = manifest.getMainAttributes();
                    return attributes.getValue("Build-Time");

                }
            }
        } catch (Exception e) {
            // Not running from a JAR or other error, return null
        }
        return null;
    }

    public ConfigDto getConfig() {
        return new ConfigDto()
                .setIcpNumber(systemCache.getIcpNumber())
                .setDailyEmailLimit(systemCache.getDailyEmailLimit())
                .setDailySmsLimit(systemCache.getDailySmsLimit())
                .setFrontendPath(systemCache.getFrontendPath())
                .setRealIpHeader(systemCache.getRealIpHeader())
                .setSiteName(systemCache.getSiteName())
                .setSiteUrl(systemCache.getSiteUrl())
                .setSmtpServers(systemCache.getSmtpServers());
    }

    @Override
    public ApiResult<Void> refreshRsaKey() {
        var keyPair = RsaUtils.initKeyPair();
        if (keyPair == null) return ApiResult.error("系统错误");
        systemRepository.updateValueByEa(keyPair.publicKey(), "sys", "rsa_public_key");
        systemRepository.updateValueByEa(SecurityUtils.aesEncrypt(keyPair.privateKey()), "sys", "rsa_private_key");
        systemCache.update();
        return ApiResult.success();

    }


    public ApiResult<Void> saveConfig(ConfigDto configDto) {
        String tmp = JsonUtils.stringify(configDto.getSmtpServers());
        assert tmp != null;
        tmp = SecurityUtils.aesEncrypt(tmp);
        systemRepository.updateValueByEa(configDto.getFrontendPath(), "devel", "frontend_path");
        systemRepository.updateValueByEa(configDto.getDailyEmailLimit(), "sys", "daily_email_limit");
        systemRepository.updateValueByEa(configDto.getDailySmsLimit(), "sys", "daily_sms_limit");
        systemRepository.updateValueByEa(configDto.getIcpNumber(), "sys", "icp_number");
        systemRepository.updateValueByEa(configDto.getRealIpHeader(), "sys", "real_ip_header");
        systemRepository.updateValueByEa(tmp, "mail", "smtp_servers");
        systemRepository.updateValueByEa(configDto.getSiteName(), "sys", "site_name");
        systemRepository.updateValueByEa(configDto.getSiteUrl(), "sys", "site_url");
        systemCache.update();
        adminLogService.addLog("修改系统配置", configDto);
        AppConfig.getContext().getBean(EmailSenderManager.class).init();
        return ApiResult.success();
    }

    public SystemInfoVo getInfo() {
        StringBuilder gc = new StringBuilder();
        for (var bean : ManagementFactory.getGarbageCollectorMXBeans()) {
            if (!gc.isEmpty()) {
                gc.append(", ");
            }
            gc.append(bean.getName());
        }
        Runtime rt = Runtime.getRuntime();
        var runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        String upTimeStr = getUpTimeStr(runtimeMXBean.getUptime());
        Properties redisProperties = Objects.requireNonNull(RedisConfig.getStringRedisTemplate().getConnectionFactory()).getConnection().serverCommands().info();
        assert redisProperties != null;
        long redisMemory = Helper.parseNumber(redisProperties.getProperty("used_memory"), Long.class);
        long redisUptime = Helper.parseNumber(redisProperties.getProperty("uptime_in_seconds"), Long.class);

        return SystemInfoVo.builder()
                .applicationDir(System.getProperty("user.dir"))
                .args(runtimeMXBean.getInputArguments().toString())
                .cpuCores(Runtime.getRuntime().availableProcessors())
                .dbVersion(systemRepository.getDbVersion())
                .buildTime(getBuildTime())
                .freeMemory(Helper.getSizeHumanReadable(rt.freeMemory()))
                .javaGc(gc.toString())
                .javaName(runtimeMXBean.getVmName())
                .javaOsName(System.getProperty("os.name"))
                .javaOsVersion(System.getProperty("os.version"))
                .javaVendor(runtimeMXBean.getVmVendor())
                .javaVersion(runtimeMXBean.getVmVersion())
                .maxMemory(Helper.getSizeHumanReadable(rt.maxMemory()))
                .redisMemory(Helper.getSizeHumanReadable(redisMemory))
                .redisVersion(redisProperties.getProperty("redis_version"))
                .valkeyVersion(redisProperties.getProperty("valkey_version"))
                .redisUptime(getUpTimeStr(redisUptime * 1000))
                .springBootVersion(SpringApplication.class.getPackage().getImplementationVersion())
                .startTime(Helper.dateFormat(new Timestamp(runtimeMXBean.getStartTime()).toLocalDateTime()))
                .totalMemory(Helper.getSizeHumanReadable(rt.totalMemory()))
                .uptime(upTimeStr)
                .vmInfo(System.getProperty("java.vm.info"))
                .build();
    }

    @Override
    public ApiResult<RsaPubKeyVo> getRsaPubKey() {
        return ApiResult.success(new RsaPubKeyVo(systemCache.getRsaPublicKey()));
    }
}
