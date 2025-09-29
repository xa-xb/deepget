package my.iris.service.ai.impl;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.openai.OpenAiChatRequestParameters;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import my.iris.cache.AiCache;
import my.iris.model.ApiResult;
import my.iris.model.ai.dto.AiChatDto;
import my.iris.model.ai.dto.AiChatQueryDto;
import my.iris.model.ai.entity.AiChatEntity;
import my.iris.model.ai.entity.AiThreadEntity;
import my.iris.model.ai.vo.AiChatAdminVo;
import my.iris.repository.ai.AiChatRepository;
import my.iris.repository.ai.AiChatThreadRepository;
import my.iris.service.ai.AiChatService;
import my.iris.service.ai.AiThreadService;
import my.iris.util.JsonUtils;
import my.iris.util.LogUtils;
import my.iris.util.UUIDUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AiChatServiceImpl implements AiChatService {
    @Resource
    AiCache aiCache;

    @Resource
    AiChatRepository aiChatRepository;

    @Resource
    AiChatThreadRepository aiChatThreadRepository;

    @Resource
    private AiThreadService aiThreadService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public List<ChatMessage> getHistory(Long threadId) {
        List<ChatMessage> chatMessages = new ArrayList<>();
        for (AiChatEntity chatEntity : aiChatRepository.findLatest(threadId, aiCache.getAiConfigDto().getMemoryCount())) {
            chatMessages.add(UserMessage.from(chatEntity.getPrompt()));
            chatMessages.add(AiMessage.from(chatEntity.getCompletion()));
        }
        return chatMessages;
    }

    @Override
    public Page<AiChatAdminVo> getPage(Pageable pageable, AiChatQueryDto queryDto) {
        return aiChatRepository.getPage(pageable, queryDto.getThreadId(), queryDto.getUsername());
    }

    @Override
    public ApiResult<String> prepare(Long userId, AiChatDto aiChatDto) {
        var uuid = UUIDUtils.generateUuidV7();
        var json = JsonUtils.stringify(aiChatDto);
        stringRedisTemplate.opsForValue().set(
                getPreparedRedisKey(userId, uuid.toString()),
                json,
                Duration.ofMinutes(1));
        return ApiResult.success(uuid.toString());
    }

    @Override
    public SseEmitter stream(Long userId, String chatUuid) {
        var sseHandler = new SseChatHandler();
        var chatDto = popChatDtoByPreparedKey(userId, chatUuid);
        if (chatDto == null) return sseHandler.sendError("session does not exist");
        AiThreadEntity threadEntity = getAiThreadEntity(userId, chatDto);
        if (threadEntity == null) {
            return sseHandler.sendError("thread does not exist");
        }
        sseHandler.init(userId, chatUuid, chatDto, threadEntity.getId());
        sseHandler.send("thread", Map.of("uuid", threadEntity.getUuid().toString()));
        var modelInstance = aiCache.createModelInstanceById(chatDto.getModelId());

        if (modelInstance == null) {
            return sseHandler.sendError("model does not exist, model id: " + chatDto.getModelId());
        }

        var messages = getHistory(threadEntity.getId());
        messages.add(UserMessage.from(chatDto.getPrompt()));
        var chatRequest = ChatRequest.builder()
                .messages(messages)
                .parameters(
                        OpenAiChatRequestParameters.builder()
                                .modelName(modelInstance.modelName())
                                .build()
                ).build();

        modelInstance.chatModel().chat(chatRequest, sseHandler);
        return sseHandler.getSseEmitter();
    }

    AiThreadEntity getAiThreadEntity(Long userId, AiChatDto chatDto) {
        var uuid = UUIDUtils.getUuid(chatDto.getThreadUuid());
        if (uuid == null) {
            var summary = chatDto.getPrompt().length() > 50
                    ? chatDto.getPrompt().substring(0, 50)
                    : chatDto.getPrompt();
            return aiThreadService.create(userId, summary);
        } else {
            return aiChatThreadRepository.findByUuidAndUserId(uuid, userId).orElse(null);
        }
    }

    AiChatDto popChatDtoByPreparedKey(Long userId, String preparedUuid) {
        var redisKey = getPreparedRedisKey(userId, preparedUuid);
        var json = stringRedisTemplate.opsForValue().get(redisKey);
        stringRedisTemplate.delete(redisKey);
        return JsonUtils.parse(json, AiChatDto.class);
    }

    String getPreparedRedisKey(Long userId, String preparedUuid) {
        return "prepare:" + userId + ":" + preparedUuid;
    }
}
