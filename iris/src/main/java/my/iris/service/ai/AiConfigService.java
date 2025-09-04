package my.iris.service.ai;

import my.iris.model.ApiResult;
import my.iris.model.ai.dto.AiConfigDto;

public interface AiConfigService {

    AiConfigDto get();
    ApiResult<Void> save(AiConfigDto aiConfigDto);
}