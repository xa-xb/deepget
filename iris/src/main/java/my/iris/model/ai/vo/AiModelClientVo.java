package my.iris.model.ai.vo;

import java.time.LocalDateTime;

public record AiModelClientVo(
        Long id,
        String name,
        String iconSvg,
        Long providerId,
        String providerName,
        String intro,
        LocalDateTime createdAt
) {
}
