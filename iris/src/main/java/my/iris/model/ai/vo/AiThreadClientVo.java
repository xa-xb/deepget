package my.iris.model.ai.vo;

import java.time.LocalDateTime;
import java.util.UUID;

public record AiThreadClientVo(
        UUID uuid,
        String title,
        LocalDateTime createdAt
) {
}
