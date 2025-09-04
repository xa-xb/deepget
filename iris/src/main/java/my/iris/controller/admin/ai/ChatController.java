package my.iris.controller.admin.ai;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import my.iris.auth.Authorize;
import my.iris.model.ApiResult;
import my.iris.model.PageVo;
import my.iris.model.ai.dto.AiChatQueryDto;
import my.iris.model.ai.vo.AiChatAdminVo;
import my.iris.service.ai.AiChatService;
import my.iris.util.DbUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("adminChatController")
@RequestMapping(path = "/admin/ai/chat", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "admin ai chat", description = "ai chat management")
public class ChatController {

    @Resource
    AiChatService chatService;

    @Authorize("/ai/chat/query")
    @PostMapping("list")
    public ApiResult<PageVo<AiChatAdminVo>> list(@RequestBody @Validated AiChatQueryDto queryDto) {
        String[] sortableColumns = new String[]{"id"};
        var page = chatService.getPage(
                PageRequest.of(
                        queryDto.getPage() - 1,
                        queryDto.getPageSize(),
                        DbUtils.getSort(sortableColumns, queryDto.getSort(), "id,desc")),
                queryDto);
        return ApiResult.success(PageVo.of(page));
    }
}
