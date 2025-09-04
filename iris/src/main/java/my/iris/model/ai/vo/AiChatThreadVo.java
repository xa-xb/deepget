package my.iris.model.ai.vo;

import java.time.LocalDateTime;

public record AiChatThreadVo(
        Long id,
        Long userId,
        String username,
        String title,
        LocalDateTime createdAt
) {
}
