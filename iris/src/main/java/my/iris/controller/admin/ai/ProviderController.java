package my.iris.controller.admin.ai;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import my.iris.auth.Authorize;
import my.iris.model.ApiResult;
import my.iris.model.IdDto;
import my.iris.model.ai.dto.AiProviderDto;
import my.iris.model.ai.vo.AiProviderVo;
import my.iris.service.ai.AiProviderService;
import my.iris.validation.groups.Add;
import my.iris.validation.groups.Edit;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/ai/provider", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "admin ai provider", description = "ai provider management")
public class ProviderController {

    @Resource
    AiProviderService aiProviderService;

    @Authorize("/my/iris/provider/add")
    @PostMapping("add")
    public ApiResult<Void> add(@RequestBody @Validated(Add.class) AiProviderDto aiProviderDto) {
        return aiProviderService.save(aiProviderDto);
    }

    @Authorize("/my/iris/provider/edit")
    @PostMapping("edit")
    public ApiResult<Void> edit(@RequestBody @Validated(Edit.class) AiProviderDto aiProviderDto) {
        return aiProviderService.save(aiProviderDto);
    }
    @Authorize("/my/iris/provider/edit")
    @PostMapping("upload")
    public ApiResult<String> upload(@RequestParam("file") MultipartFile file) {

        return aiProviderService.upload(file);
    }

    @Authorize("/my/iris/provider/delete")
    @PostMapping("delete")
    public ApiResult<Void> delete(@RequestBody @Validated IdDto idDto) {
        return aiProviderService.delete(idDto);
    }

    @Authorize({"/my/iris/provider/query"})
    @PostMapping("api_compatibles")
    public ApiResult<List<String>> apiCompatibles() {
        return ApiResult.success(aiProviderService.getApiCompatibles());
    }

    @Authorize({"/my/iris/provider/query", "/my/iris/model/query"})
    @PostMapping("list")
    public ApiResult<List<AiProviderVo>> list() {
        return ApiResult.success(aiProviderService.getList());
    }
}
