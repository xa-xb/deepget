package my.iris.model.ai.vo;

import java.util.List;

public record AiThreadsClientVo(
        String threadUuid,
        List<AiChatClientVo> chats
) {
}
