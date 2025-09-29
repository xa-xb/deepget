package my.iris.service.ai.impl;

import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import my.iris.model.ApiResult;
import my.iris.model.ai.dto.AiThreadQueryDto;
import my.iris.model.ai.entity.AiThreadEntity;
import my.iris.model.ai.vo.AiChatClientVo;
import my.iris.model.ai.vo.AiChatThreadVo;
import my.iris.model.ai.vo.AiThreadClientVo;
import my.iris.repository.ai.AiChatRepository;
import my.iris.repository.ai.AiChatThreadRepository;
import my.iris.service.ai.AiThreadService;
import my.iris.service.user.UserLogService;
import my.iris.util.DbUtils;
import my.iris.util.UUIDUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class AiThreadImpl implements AiThreadService {
    @Resource
    AiChatThreadRepository aiChatThreadRepository;

    @Resource
    AiChatRepository aiChatRepository;

    @Resource
    private UserLogService userLogService;

    @Override
    public Page<AiChatThreadVo> getPage(Pageable pageable, AiThreadQueryDto queryDto) {
        return aiChatThreadRepository.getPage(pageable,
                queryDto.getStatus(),
                DbUtils.getLikeWords(queryDto.getTitle()),
                queryDto.getUsername());
    }

    @Override
    public AiThreadEntity create(Long userId, String title) {
        var entity = new AiThreadEntity()
                .setUserId(userId)
                .setUuid(UUIDUtils.generateUuidV7())
                .setTitle(title);
        return aiChatThreadRepository.saveAndFlush(entity);
    }

    @Override
    public ApiResult<List<AiChatClientVo>> get(UUID threadUuid, Long userId) {
        return aiChatThreadRepository.getClientThread(threadUuid, userId)
                .map(threadEntity -> {
                    var list = aiChatRepository.findByThreadIdOrderById(threadEntity.getId())
                            .stream()
                            .map(item -> new AiChatClientVo(item.getModelId(), item.getUuid().toString(),
                                    threadUuid.toString(), item.getPrompt(), item.getCompletion(), item.getError(),
                                    item.getInputTokenCount(), item.getOutputTokenCount(), item.getTotalTokenCount())
                            ).toList();
                    return ApiResult.success(list);
                }).orElseGet(() -> ApiResult.error("thread not found: " + threadUuid));
    }

    @Override
    public ApiResult<Void> rename(UUID threadUuid, String newTitle, Long userId) {
        return aiChatThreadRepository.getClientThread(threadUuid, userId)
                .map(thread -> {
                    userLogService.addLog("rename_thread", Map.of(
                            "thread_id", thread.getId(),
                            "oldName", thread.getTitle(),
                            "newName", newTitle
                    ));
                    thread.setTitle(newTitle);
                    return ApiResult.<Void>success();
                })
                .orElseGet(() -> ApiResult.error("thread not found: " + threadUuid));
    }

    @Override
    public ApiResult<Void> delete(UUID threadUuid, Long userId) {
        return aiChatThreadRepository.getClientThread(threadUuid, userId)
                .map(thread -> {
                    thread.setDeletedAt(LocalDateTime.now());
                    return ApiResult.<Void>success();
                })
                .orElseGet(() -> ApiResult.error("thread not found: " + threadUuid));
    }

    @Override
    public ApiResult<Void> deleteAll(Long userId) {
        aiChatThreadRepository.deleteAllByUserId(userId);
        return ApiResult.success();
    }

    @Override
    public List<AiThreadClientVo> listClientThreads(Long userId) {
        return aiChatThreadRepository.listClientThreads(userId);
    }
}
