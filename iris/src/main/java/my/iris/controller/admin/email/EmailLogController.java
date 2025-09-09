package my.iris.controller.admin.email;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import my.iris.auth.Authorize;
import my.iris.model.ApiResult;
import my.iris.model.PageVo;
import my.iris.model.QueryDto;
import my.iris.model.email.dto.EmailQueryDto;
import my.iris.model.email.vo.EmailLogVo;
import my.iris.service.email.EmailLogService;
import my.iris.util.DbUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/admin/email/emailLog", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "admin email emailLog", description = "email emailLog management")
public class EmailLogController {

    @Resource
    EmailLogService emailLogService;

    @Authorize("/email/emailLog/query")
    @PostMapping("list")
    public ApiResult<PageVo<EmailLogVo>> list(@RequestBody @Validated EmailQueryDto queryDto) {
        String[] sortableColumns = new String[]{"id"};
        var page = emailLogService.getPage(
                PageRequest.of(
                        queryDto.getPage() - 1,
                        queryDto.getPageSize(),
                        DbUtils.getSort(sortableColumns, queryDto.getSort(), "id,desc")),
                queryDto);
        return ApiResult.success(PageVo.of(page));
    }
}
