package my.iris.controller.admin.system;

import io.swagger.v3.oas.annotations.tags.Tag;
import my.iris.auth.Authorize;
import my.iris.model.ApiResult;
import my.iris.model.QueryDto;
import my.iris.model.PageVo;
import my.iris.model.system.vo.AdminLogVo;
import my.iris.service.system.AdminLogService;
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

@RestController("sysLogController")
@RequestMapping(path = "/admin/system/log", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "admin system log", description = "admin system log controller")
public class LogController {
    @Resource
    AdminLogService adminLogService;

    @Authorize("/system/log/query")
    @Operation(summary = "获取管理日志", description = """
            required fields: name,ip,operation,sort,page,pageSize
            Optional values for sort: id, duration
            """)
    @PostMapping("list")
    public ApiResult<PageVo<AdminLogVo>> list(@RequestBody @Validated QueryDto query) {

        String[] sortableColumns = new String[]{"id", "duration"};
        return ApiResult.success(PageVo.of(adminLogService.getPage(
                query.name(),
                query.ip(),
                query.operation(),
                PageRequest.of(
                        query.page() - 1,
                        query.pageSize(),
                        DbUtils.getSort(sortableColumns, query.sort(), "id,desc"))
        )));
    }

}
