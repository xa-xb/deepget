package my.iris.controller.admin.system;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import my.iris.auth.Authorize;
import my.iris.model.ApiResult;
import my.iris.model.IdDto;
import my.iris.model.system.entity.AdminMenuEntity;
import my.iris.service.system.AdminMenuService;
import my.iris.validation.groups.Add;
import my.iris.validation.groups.Edit;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/system/menu", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "admin system menu", description = "admin system menu controller")
public class MenuController {

    @Resource
    AdminMenuService adminMenuService;

    @Authorize("/system/menu/add")
    @PostMapping("add")
    public ApiResult<Void> add(
            @RequestBody @Validated(Add.class) AdminMenuEntity adminMenuEntity) {
        return adminMenuService.save(adminMenuEntity);
    }

    @Authorize("/system/menu/delete")
    @PostMapping("delete")
    public ApiResult<Void> delete(@RequestBody @Validated IdDto idDto) {
        return adminMenuService.delete(idDto.id());
    }


    @Authorize("/system/menu/edit")
    @PostMapping("edit")
    public ApiResult<Void> edit(
            @RequestBody @Validated(Edit.class) AdminMenuEntity adminMenuEntity) {
        return adminMenuService.save(adminMenuEntity);
    }

    @Authorize({"/system/menu/query", "/devel/gen/query"})
    @PostMapping("list")
    public ApiResult<List<AdminMenuEntity>> list(@RequestBody(required = false) AdminMenuEntity condition) {
        return adminMenuService.getList(condition);
    }
}
