package my.iris.service.ai.impl;

import dev.langchain4j.exception.InternalServerException;
import dev.langchain4j.exception.RateLimitException;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import lombok.Getter;
import my.iris.config.AppConfig;
import my.iris.model.ai.dto.AiChatDto;
import my.iris.model.ai.entity.AiChatEntity;
import my.iris.repository.ai.AiChatRepository;
import my.iris.util.LogUtils;
import my.iris.util.UUIDUtils;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

public class SseChatHandler implements StreamingChatResponseHandler {


    @SuppressWarnings("unused,FieldCanBeLocal")
    private Long userId;
    private String chatUuid;
    private AiChatDto chatDto;
    private Long threadId;
    private TransactionTemplate transactionTemplate;
    private AiChatRepository aiChatRepository;
    @Getter
    private final SseEmitter sseEmitter;
    private volatile boolean isCompleted = false;
    private Long startTime;

    public SseChatHandler() {
        sseEmitter = new SseEmitter(600_000L);
        sseEmitter.onCompletion(() -> isCompleted = true);
        sseEmitter.onTimeout(this::sendEnd);

    }

    public void init(Long userId, String chatUuid, AiChatDto chatDto, Long threadId) {
        this.userId = userId;
        this.chatUuid = chatUuid;
        this.chatDto = chatDto;
        this.threadId = threadId;
        startTime = System.currentTimeMillis();
        transactionTemplate = AppConfig.getContext().getBean(TransactionTemplate.class);
        aiChatRepository = AppConfig.getContext().getBean(AiChatRepository.class);
    }

    public void complete() {
        if (!isCompleted) {
            isCompleted = true;
            sseEmitter.complete();
        }
    }

    @Override
    public void onPartialResponse(String s) {
        send(s);
    }

    @Override
    public void onCompleteResponse(ChatResponse chatResponse) {
        transactionTemplate.execute(status -> {
            int duration = (int) (System.currentTimeMillis() - startTime);
            long inputToken = 0, outputToken = 0, totalTokens = 0;
            // tokenUsage maybe null
            var tokenUsage = chatResponse.tokenUsage();
            if (tokenUsage != null) {
                inputToken = tokenUsage.inputTokenCount();
                outputToken = tokenUsage.outputTokenCount();
                totalTokens = tokenUsage.totalTokenCount();
            }
            // The <think> tag is sometimes not properly closed.
            var completion = chatResponse.aiMessage().text();
            if (completion.contains("<think>") && !completion.contains("</think>")) {
                completion += "</think>";
            }
            var chatEntity = new AiChatEntity()
                    .setUuid(UUIDUtils.getUuid(chatUuid))
                    .setThreadId(threadId)
                    .setPrompt(chatDto.getPrompt())
                    .setCompletion(completion)
                    .setModelId(chatDto.getModelId())
                    .setInputTokenCount(inputToken)
                    .setOutputTokenCount(outputToken)
                    .setTotalTokenCount(totalTokens)
                    .setCreditsUsed(0L)
                    .setCreditsRemaining(0L)
                    .setDuration(duration);
            aiChatRepository.save(chatEntity);
            return null;
        });
        sendEnd();
    }

    @Override
    public void onError(Throwable throwable) {
        if (isCompleted) return;
        switch (throwable) {
            case RateLimitException ignored -> sendError("Too many requests. Please wait and try again shortly.");
            case InternalServerException ignored -> sendError("Internal Server Error.");
            case IllegalStateException ignored -> complete();
            case null, default -> LogUtils.warn(getClass(), throwable, throwable);
        }
    }

    public SseEmitter sendError(Object message) {
        send("error", message);
        complete();
        return sseEmitter;
    }

    public void sendEnd() {
        send("end", "done");
        complete();
    }

    public void send(Object object) {
        send(null, object);
    }

    public void send(String name, Object object) {
        if (isCompleted) return;
        if (object == null) return;
        Object data = object instanceof String ? Map.of("m", object) : object;
        name = name == null ? "message" : name;
        try {
            sseEmitter.send(SseEmitter.event().name(name).data(data));
        } catch (IOException ignored) {
            complete();
        }
    }

}
