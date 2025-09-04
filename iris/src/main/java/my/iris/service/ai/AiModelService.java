package my.iris.service.ai;

import my.iris.model.ApiResult;
import my.iris.model.IdDto;
import my.iris.model.ai.dto.AiModelDto;
import my.iris.model.ai.vo.AiModelClientVo;
import my.iris.model.ai.vo.AiModelVo;

import java.util.List;

public interface AiModelService {
    ApiResult<Void> delete(IdDto idDto);

    List<AiModelVo> getList();

    ApiResult<Void> save(AiModelDto aiModelDto);
}
