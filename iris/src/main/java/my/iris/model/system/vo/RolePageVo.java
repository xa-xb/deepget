package my.iris.model.system.vo;

import my.iris.model.system.entity.AdminRoleEntity;

import java.util.List;

public record RolePageVo(
        List<AdminRoleEntity> rows,
        List<RoleDataVo> roleData
) {
}
