package my.iris.model.ai.vo;

import java.time.LocalDateTime;

public record AiChatAdminVo(
        Long id,
        Long userId,
        String userName,
        Long threadId,
        String provider,
        String model,
        String prompt,
        String completion,
        Long inputTokens,
        Long outputTokens,
        Long totalTokens,
        Integer duration,
        LocalDateTime createdAt
) {
}
