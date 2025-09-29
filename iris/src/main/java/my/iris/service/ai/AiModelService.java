package my.iris.service.ai;

import my.iris.model.ApiResult;
import my.iris.model.IdDto;
import my.iris.model.ai.dto.AiModelDto;
import my.iris.model.ai.dto.AiModelQueryDto;
import my.iris.model.ai.dto.AiThreadQueryDto;
import my.iris.model.ai.vo.AiChatThreadVo;
import my.iris.model.ai.vo.AiModelClientVo;
import my.iris.model.ai.vo.AiModelVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AiModelService {
    ApiResult<Void> delete(IdDto idDto);

    Page<AiModelVo> getPage(AiModelQueryDto queryDto);

    ApiResult<Void> save(AiModelDto aiModelDto);
}
