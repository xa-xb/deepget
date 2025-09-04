package my.iris.controller.admin.ai;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import my.iris.auth.Authorize;
import my.iris.model.ApiResult;
import my.iris.model.IdDto;
import my.iris.model.PageVo;
import my.iris.model.QueryDto;
import my.iris.model.ai.dto.AiConfigDto;
import my.iris.model.ai.vo.AiConfigVo;
import my.iris.service.ai.AiConfigService;
import my.iris.util.DbUtils;
import my.iris.validation.groups.Add;
import my.iris.validation.groups.Edit;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/admin/ai/config", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "admin ai aiConfig", description = "ai aiConfig management")
public class AiConfigController {

    @Resource
    AiConfigService aiConfigService;

    @Authorize("/ai/config/query")
    @PostMapping("get")
    public ApiResult<AiConfigDto> add() {
        return ApiResult.success(aiConfigService.get());
    }

    @Authorize("/ai/config/edit")
    @PostMapping("save")
    public ApiResult<Void> save(@RequestBody @Validated(Edit.class) AiConfigDto aiConfigDto) {
        return aiConfigService.save(aiConfigDto);
    }
}
