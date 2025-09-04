package my.iris.model.system.vo;

import lombok.Builder;

@Builder
public record SystemInfoVo(
        String buildTime,
        String dbVersion,
        String javaOsName,
        String javaOsVersion,
        String javaName,
        String javaVersion,
        String javaVendor,
        String vmInfo,
        String args,
        Integer cpuCores,
        String javaGc,
        String applicationDir,
        String springBootVersion,
        String redisVersion,
        String valkeyVersion,
        String redisMemory,
        String redisUptime,
        String freeMemory,
        String maxMemory,
        String totalMemory,
        String startTime,
        String uptime
) {
}