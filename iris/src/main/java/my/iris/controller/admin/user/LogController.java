package my.iris.controller.admin.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import my.iris.auth.Authorize;
import my.iris.model.ApiResult;
import my.iris.model.QueryDto;
import my.iris.model.PageVo;
import my.iris.model.user.vo.UserLogVo;
import my.iris.service.user.UserLogService;
import my.iris.util.DbUtils;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("userLogController")
@RequestMapping(path = "/admin/user/log", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "admin user log", description = "admin user log controller")
public class LogController {
    @Resource
    UserLogService userLogService;

    @Authorize("/user/log/query")
    @Operation(summary = "获取用户日志", description = """
            required fields: name, ip, operation, sort, page, pageSize.<br>
            Optional values for sort: id, duration
            """)
    @PostMapping("list")
    public ApiResult<PageVo<UserLogVo>> list(@RequestBody @Validated QueryDto queryDto) {
        String[] sortableColumns = new String[]{"id", "duration"};
        var page = userLogService.getPage(
                queryDto.name(),
                queryDto.ip(),
                queryDto.operation(),
                PageRequest.of(
                        queryDto.page() - 1,
                        queryDto.pageSize(),
                        DbUtils.getSort(sortableColumns, queryDto.sort(), "id,desc")));
        return ApiResult.success(PageVo.of(page));
    }
}
