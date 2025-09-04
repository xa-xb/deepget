package my.iris.model.ai.vo;

import java.time.LocalDateTime;

public record AiConfigVo(
        Long id,
        String name,
        LocalDateTime createdAt
) {
}
