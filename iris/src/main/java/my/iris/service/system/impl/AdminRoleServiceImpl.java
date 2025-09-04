package my.iris.service.system.impl;

import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import my.iris.model.ApiResult;
import my.iris.model.system.entity.AdminRoleEntity;
import my.iris.repository.system.AdminMenuRepository;
import my.iris.repository.system.AdminRepository;
import my.iris.repository.system.AdminRoleRepository;
import my.iris.service.system.AdminLogService;
import my.iris.service.system.AdminRoleService;
import my.iris.util.DbUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class AdminRoleServiceImpl implements AdminRoleService {

    @Resource
    AdminLogService adminLogService;

    @Resource
    AdminMenuRepository adminMenuRepository;

    @Resource
    AdminRepository adminRepository;

    @Resource
    AdminRoleRepository adminRoleRepository;

    @Override
    public ApiResult<Void> delete(Long roleId) {
        var entity = DbUtils.findByIdForUpdate(AdminRoleEntity.class, roleId);
        if (entity == null) {
            return ApiResult.error("角色不存在");
        }
        if (roleId == 1L) {
            return ApiResult.error("不能删除系统内置角色");
        }
        DbUtils.delete(entity);
        adminRepository.removeRoleId(roleId);
        return ApiResult.success();
    }

    /**
     * get authorize ids by roles ids,
     *
     * @param roleIds role ids
     * @return auth ids
     */
    @Override
    public List<Long> getAuthorizeIdsByRolesId(List<Long> roleIds) {
        List<Long> authIds = new ArrayList<>();
        adminRoleRepository.findAllByIdIn(roleIds).forEach(adminRoleEntity -> {
            authIds.addAll(adminRoleEntity.getAuthIds());
        });
        return authIds;
    }

    @Override
    public List<AdminRoleEntity> getList() {
        return adminRoleRepository.findAllByOrderByOrderNum();
    }

    @Override
    public ApiResult<Void> save(AdminRoleEntity adminRoleEntity) {
        if (Objects.equals(adminRoleEntity.getId(), 1L)) {
            return ApiResult.error("不能修改系统内置角色");
        }
        var existEntity = adminRoleRepository.findByName(adminRoleEntity.getName()).orElse(null);
        if (existEntity != null && !Objects.equals(adminRoleEntity.getId(), existEntity.getId())) {
            return ApiResult.error("角色名称已存在");
        }
        adminRoleEntity.setAuthIds(
                adminMenuRepository.getAvailableAuthorize(adminRoleEntity.getAuthIds()));
        adminRoleRepository.save(adminRoleEntity);
        adminLogService.addLog(adminRoleEntity.getId() == null ? "添加角色" : "修改角色", adminRoleEntity);
        return ApiResult.success();
    }
}
