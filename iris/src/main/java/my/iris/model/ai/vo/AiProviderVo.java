package my.iris.model.ai.vo;

import my.iris.util.SecurityUtils;

import java.time.LocalDateTime;

public record AiProviderVo(
        Long id,
        String name,
        String iconSvg,
        String apiCompatible,
        String url,
        Boolean openRouter,
        String apiUrl,
        String apiKey,
        Integer orderNum,
        LocalDateTime createdAt
) {
    public AiProviderVo {
        apiKey = SecurityUtils.aesDecrypt(apiKey);
    }
}
