package my.iris.controller.admin.ai;

import my.iris.auth.Authorize;
import my.iris.model.ApiResult;
import my.iris.model.IdDto;
import my.iris.model.PageVo;
import my.iris.model.QueryDto;
import my.iris.model.ai.dto.AiModelDto;
import my.iris.model.ai.dto.AiModelQueryDto;
import my.iris.model.ai.vo.AiModelVo;
import my.iris.service.ai.AiModelService;
import my.iris.util.DbUtils;
import my.iris.validation.groups.Add;
import my.iris.validation.groups.Edit;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("adminModelController")
@RequestMapping(path = "/admin/ai/model", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "admin ai model", description = "ai 模型管理")
public class ModelController {
    @Resource
    AiModelService aiModelService;

    @Authorize("/ai/model/add")
    @PostMapping("add")
    public ApiResult<Void> add(@RequestBody @Validated(Add.class) AiModelDto aiModelDto) {
        return aiModelService.save(aiModelDto);
    }

    @Authorize("/ai/model/edit")
    @PostMapping("edit")
    public ApiResult<Void> edit(@RequestBody @Validated(Edit.class) AiModelDto aiModelDto) {
        return aiModelService.save(aiModelDto);
    }

    @Authorize("/ai/model/delete")
    @PostMapping("delete")
    public ApiResult<Void> delete(@RequestBody @Validated IdDto idDto) {
        return aiModelService.delete(idDto);
    }

    @Authorize("/ai/model/query")
    @PostMapping("list")
    public ApiResult<PageVo<AiModelVo>> list(@RequestBody @Validated AiModelQueryDto queryDto) {
        return ApiResult.success(PageVo.of(aiModelService.getPage(queryDto)));
    }
}
