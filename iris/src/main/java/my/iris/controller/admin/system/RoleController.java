package my.iris.controller.admin.system;

import io.swagger.v3.oas.annotations.tags.Tag;
import my.iris.auth.Authorize;
import my.iris.model.ApiResult;
import my.iris.model.IdDto;
import my.iris.model.ListVo;
import my.iris.model.system.entity.AdminRoleEntity;
import my.iris.model.system.vo.RoleDataVo;
import my.iris.model.system.vo.RolePageVo;
import my.iris.model.system.vo.SimpleRoleVo;
import my.iris.repository.system.AdminMenuRepository;
import my.iris.service.system.AdminLogService;
import my.iris.service.system.AdminMenuService;
import my.iris.service.system.AdminRoleService;
import my.iris.validation.groups.Add;
import my.iris.validation.groups.Edit;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(path = "/admin/system/role", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "admin system role", description = "admin system role controller")
public class RoleController {

    @Resource
    AdminLogService adminLogService;

    @Resource
    AdminMenuRepository adminMenuRepository;

    @Resource
    AdminRoleService adminRoleService;
    @Autowired
    private AdminMenuService adminMenuService;

    @Authorize("/system/role/add")
    @PostMapping("add")
    public ApiResult<Void> add(
            @RequestBody @Validated(Add.class) AdminRoleEntity adminRoleEntity) {
        return (adminRoleService.save(adminRoleEntity));
    }

    @Authorize("/system/role/delete")
    @PostMapping("delete")
    public ApiResult<Void> delete(@Validated @RequestBody IdDto idDto) {
        return adminRoleService.delete(idDto.id());
    }

    @Authorize("/system/role/query")
    @PostMapping("list")
    public ApiResult<RolePageVo> list() {
        return ApiResult.success(new RolePageVo(
                adminRoleService.getList(),
                getRoleData()));
    }

    @Authorize("/system/admin/query")
    @PostMapping("list_simple")
    public ApiResult<ListVo<SimpleRoleVo>> listSimple() {
        List<SimpleRoleVo> simpleRoleVos = new ArrayList<>();
        adminRoleService.getList().forEach(item -> {
            simpleRoleVos.add(new SimpleRoleVo(item.getId(), item.getName()));
        });
        return ApiResult.success(new ListVo<>(simpleRoleVos));
    }

    @Authorize("/system/role/edit")
    @PostMapping("edit")
    public ApiResult<Void> edit(
            @RequestBody @Validated(Edit.class) AdminRoleEntity adminRoleEntity) {
        return adminRoleService.save(adminRoleEntity);
    }

    public List<RoleDataVo> getRoleData() {
        var rows = adminMenuService.getList(null).data();
        List<RoleDataVo> roleDataVos = new ArrayList<>();
        rows.stream().filter(item -> item.getType() == 1).forEach(item -> {
            List<RoleDataVo> childrenMenu = new ArrayList<>();
            RoleDataVo mapDirectory = new RoleDataVo(item.getId(), item.getName(), childrenMenu);
            rows.stream().filter(itemMenu -> Objects.equals(itemMenu.getParentId(), item.getId())).forEach(itemMenu -> {
                List<RoleDataVo> childrenAuthorize = new ArrayList<>();
                RoleDataVo mapMenu = new RoleDataVo(itemMenu.getId(), itemMenu.getName(), childrenAuthorize);
                rows.stream().filter(itemAuthorize -> Objects.equals(itemAuthorize.getParentId(), itemMenu.getId()))
                        .forEach(itemAuthorize -> {
                            RoleDataVo mapAuthorize = new RoleDataVo(itemAuthorize.getId(), itemAuthorize.getName(), null);
                            childrenAuthorize.add(mapAuthorize);
                        });
                childrenMenu.add(mapMenu);
            });
            roleDataVos.add(mapDirectory);
        });
        return roleDataVos;
    }
}
