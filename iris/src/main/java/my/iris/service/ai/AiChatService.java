package my.iris.service.ai;


import dev.langchain4j.data.message.ChatMessage;
import my.iris.model.ApiResult;
import my.iris.model.ai.dto.AiChatDto;
import my.iris.model.ai.dto.AiChatQueryDto;
import my.iris.model.ai.vo.AiChatAdminVo;
import my.iris.model.ai.vo.AiChatClientVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface AiChatService {
    List<ChatMessage> getHistory(Long threadId);
    Page<AiChatAdminVo> getPage(Pageable pageable, AiChatQueryDto queryDto);

    ApiResult<String> prepare(Long userId, AiChatDto aiChatDto);

    /**
     * Establishes an SSE (Server-Sent Events) stream using the UUID returned by the /prepare endpoint.
     *
     * @param  userId the ID of the user requesting the stream
     * @param uuid the UUID returned from the /prepare endpoint
     * @return a {@link SseEmitter} for streaming events to the client
     */
    SseEmitter stream(Long userId, String uuid);

}
