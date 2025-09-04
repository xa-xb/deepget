package my.iris.controller.admin.system;

import io.swagger.v3.oas.annotations.tags.Tag;
import my.iris.auth.Authorize;
import my.iris.model.ApiResult;
import my.iris.model.QueryDto;
import my.iris.model.PageVo;
import my.iris.model.system.dto.AdminDto;
import my.iris.model.system.entity.AdminEntity;
import my.iris.model.system.vo.AdminVo;
import my.iris.service.system.AdminLogService;
import my.iris.service.system.AdminService;
import my.iris.validation.groups.Add;
import my.iris.validation.groups.Delete;
import my.iris.validation.groups.Edit;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/admin/system/admin", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "admin system admin", description = "admin system admin controller")
public class AdminController {

    @Resource
    AdminLogService adminLogService;
    @Resource
    AdminService adminService;

    @Authorize("/system/admin/add")
    @Operation(summary = "新增管理员", description = "需要的字段：account")
    @PostMapping("add")
    public ApiResult<Void> add(@RequestBody @Validated(Add.class) AdminDto adminDto) {
        return adminService.save(adminDto);
    }

    @Authorize("/system/admin/delete")
    @PostMapping("delete")
    public ApiResult<Void> delete(
            @RequestBody @Validated(Delete.class) AdminEntity entity) {
        return adminService.deleteByUserId(entity.getUserId());
    }


    @Authorize("/system/admin/query")
    @PostMapping("list")
    public ApiResult<PageVo<AdminVo>> list(@RequestBody @Validated QueryDto query) {

        var page = adminService.getPage(null,
                PageRequest.of(
                        query.page() - 1,
                        query.pageSize()));
        return ApiResult.success(PageVo.of(page));
    }

    @Authorize("/system/admin/edit")
    @PostMapping("edit")
    public ApiResult<Void> edit(
            @RequestBody @Validated(Edit.class) AdminDto adminDto) {
        return adminService.save(adminDto);
    }
}
