package my.iris.service.system.impl;


import my.iris.auth.UserToken;
import my.iris.model.ApiResult;
import my.iris.model.system.dto.AdminDto;
import my.iris.model.system.entity.AdminEntity;
import my.iris.model.system.vo.AdminVo;
import my.iris.model.user.entity.UserEntity;
import my.iris.repository.system.AdminRepository;
import my.iris.repository.system.AdminRoleRepository;
import my.iris.service.system.AdminLogService;
import my.iris.service.system.AdminService;
import my.iris.service.user.UserService;
import my.iris.util.DbUtils;
import my.iris.util.TaskContext;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;


@Service
@Transactional
public class AdminServiceImpl implements AdminService {
    @Resource
    AdminLogService adminLogService;

    @Resource
    UserService userService;

    @Resource
    AdminRoleRepository adminRoleRepository;
    @Resource
    AdminRepository adminRepository;


    public AdminEntity findByUserId(Long userId) {
        return adminRepository.findOne(Example.of(
                new AdminEntity().setUserId(userId)
        )).orElse(null);
    }

    public ApiResult<Void> deleteByUserId(Long userId) {
        var entity = adminRepository.findByUserIdForUpdate(userId);
        if (entity == null) {
            return ApiResult.error("管理账号不存在");
        }
        adminRepository.delete(entity);
        adminLogService.addLog("删除管理账号", entity);
        return ApiResult.success();
    }

    public Page<AdminVo> getPage(String name, Pageable pageable) {
        return adminRepository.getPage(DbUtils.getLikeWords(name), pageable);
    }

    public UserService.SignInResult signIn(String account, String password) {
        var loginResult = userService.findUserByAccountAndPassword(account, password);
        if (loginResult.getError() != null) {
            return loginResult;
        }
        var adminUserEntity = adminRepository.findByUserIdForUpdate(loginResult.getUserEntity().getId());
        if (adminUserEntity == null) {
            return loginResult.setError("该账号不是管理员");
        }
        if (!adminUserEntity.getEnabled()) {
            return loginResult.setError("该账号已被禁用");
        }
        new UserToken(loginResult.getUserEntity())
                .save(TaskContext.getSession());
        signInLog(adminUserEntity);
        return loginResult;
    }

    public void signInLog(AdminEntity adminEntity) {
        adminEntity.setLastSignInAt(LocalDateTime.now()).setLastSignInIp(TaskContext.getClientIp());
        adminLogService.addLog("sign_in", adminEntity);
    }


    public void signOut() {
        var adminToken = TaskContext.getAdminToken();
        if (adminToken != null) {
            adminLogService.addLog("退出登录", Map.of("userid", adminToken.getUserId()));
            TaskContext.getSession().clear();
        }
    }

    public ApiResult<Void> save(AdminDto adminDto) {
        var isNew = adminDto.id() == null;
        var userEntity = isNew
                ? userService.findUserByAccount(adminDto.account())
                : DbUtils.findByIdForUpdate(UserEntity.class, adminDto.userId());
        if (userEntity == null) {
            return ApiResult.error("用户不存在");
        }
        AdminEntity adminEntity;
        if (isNew) {
            if (adminRepository.findByUserId(adminDto.userId()).isPresent()) {
                return ApiResult.error("管理账号已存在");
            }
            adminEntity = new AdminEntity();
        } else {
            adminEntity = DbUtils.findByIdForUpdate(AdminEntity.class, adminDto.id());
            if (adminEntity == null) {
                return ApiResult.error("管理账号不存在");
            }
        }
        adminEntity.setUserId(userEntity.getId())
                .setEnabled(adminDto.enabled())
                .setRoleIds(adminRoleRepository.getAvailableRolesId(adminDto.roleIds()))
                .setTrueName(adminDto.trueName())
                .setCreatedIp(TaskContext.getClientIp());
        adminRepository.save(adminEntity);
        adminLogService.addLog(isNew ? "新建管理账号" : "编辑管理账号", adminDto);
        return ApiResult.success();
    }
}
