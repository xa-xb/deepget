package my.iris.controller.api.v1.ai;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import my.iris.cache.AiCache;
import my.iris.model.ApiResult;
import my.iris.model.ai.vo.AiModelClientVo;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/ai/model", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "user ai model", description = "user ai model controller")
public class ModelController {

    @Resource
    AiCache aiCache;

    @Operation(summary = "list models", description = "list models")
    @PostMapping("list")
    public ApiResult<List<AiModelClientVo>> list() {
        return ApiResult.success(aiCache.getAiModelClientVos());
    }

}
