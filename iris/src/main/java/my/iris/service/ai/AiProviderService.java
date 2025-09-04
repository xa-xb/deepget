package my.iris.service.ai;

import my.iris.model.ApiResult;
import my.iris.model.IdDto;
import my.iris.model.ai.dto.AiProviderDto;
import my.iris.model.ai.vo.AiProviderVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AiProviderService {
    ApiResult<Void> delete(IdDto idDto);

    /**
     * Get the list of AI providers
     * @return List of AI providers
     */
    List<String> getApiCompatibles();
    List<AiProviderVo> getList();
    ApiResult<Void> save(AiProviderDto aiProviderDto);
    ApiResult<String> upload(MultipartFile file);
}
