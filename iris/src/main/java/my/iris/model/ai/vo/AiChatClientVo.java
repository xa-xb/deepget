package my.iris.model.ai.vo;

public record AiChatClientVo(
        Long modelId,
        String uuid,
        String threadUuid,
        String prompt,
        String completion,
        String error,
        Long inputTokens,
        Long outputTokens,
        Long totalTokens
) {
}
