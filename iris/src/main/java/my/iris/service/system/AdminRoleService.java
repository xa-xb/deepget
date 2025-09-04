package my.iris.service.system;

import my.iris.model.ApiResult;
import my.iris.model.system.entity.AdminRoleEntity;

import java.util.List;

public interface AdminRoleService {
    ApiResult<Void> delete(Long id);

    List<Long> getAuthorizeIdsByRolesId(List<Long> roleIds);

    List<AdminRoleEntity> getList();

    ApiResult<Void> save(AdminRoleEntity adminRoleEntity);
}