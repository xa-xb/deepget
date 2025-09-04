package my.iris.model.ai.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AiModelVo(
        Long id,
        String name,
        String iconSvg,
        String sysName,
        String openRouterName,
        Long providerId,
        String providerName,
        BigDecimal cnyPerToken,
        String intro,
        Integer orderNum,
        Boolean enabled,
        Boolean free,
        LocalDateTime createdAt
) {
}
