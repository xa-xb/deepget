package my.iris.controller.admin.ai;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import my.iris.auth.Authorize;
import my.iris.model.ApiResult;
import my.iris.model.IdDto;
import my.iris.model.PageVo;
import my.iris.model.ai.dto.AiThreadQueryDto;
import my.iris.model.ai.vo.AiChatThreadVo;
import my.iris.service.ai.AiThreadService;
import my.iris.util.DbUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("adminThreadController")
@RequestMapping(path = "/admin/ai/thread", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "admin ai chat thread", description = "ai chat thread management")
public class ThreadController {

    @Resource
    AiThreadService aiThreadService;

    @Authorize("/ai/thread/delete")
    @PostMapping("delete")
    public ApiResult<Void> delete(@RequestBody @Validated IdDto idDto) {
        return null;
    }

    @Authorize("/ai/thread/query")
    @PostMapping("list")
    public ApiResult<PageVo<AiChatThreadVo>> list(@RequestBody @Validated AiThreadQueryDto queryDto) {
        String[] sortableColumns = new String[]{"id"};
        var page = aiThreadService.getPage(
                PageRequest.of(
                        queryDto.getPage() - 1,
                        queryDto.getPageSize(),
                        DbUtils.getSort(sortableColumns, queryDto.getSort(), "id,desc")),
                queryDto);
        return ApiResult.success(PageVo.of(page));
    }
}
