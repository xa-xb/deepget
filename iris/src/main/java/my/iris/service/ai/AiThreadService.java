package my.iris.service.ai;

import my.iris.model.ApiResult;
import my.iris.model.ai.dto.AiThreadQueryDto;
import my.iris.model.ai.entity.AiThreadEntity;
import my.iris.model.ai.vo.AiChatClientVo;
import my.iris.model.ai.vo.AiChatThreadVo;
import my.iris.model.ai.vo.AiThreadClientVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface AiThreadService {
    Page<AiChatThreadVo> getPage(Pageable pageable, AiThreadQueryDto queryDto);

    AiThreadEntity create(Long userId, String title);
    ApiResult<List<AiChatClientVo>> get(UUID threadUuid, Long userId);
    ApiResult<Void> rename(UUID threadUuid, String newTitle, Long userId);
    ApiResult<Void> delete(UUID threadUuid, Long userId);
    ApiResult<Void> deleteAll(Long userId);
    List<AiThreadClientVo> listClientThreads(Long userId);
}
