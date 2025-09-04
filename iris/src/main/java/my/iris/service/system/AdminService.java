package my.iris.service.system;

import my.iris.model.ApiResult;
import my.iris.model.system.dto.AdminDto;
import my.iris.model.system.entity.AdminEntity;
import my.iris.model.system.vo.AdminVo;
import my.iris.service.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminService {
    ApiResult<Void> deleteByUserId(Long userId);

    Page<AdminVo> getPage(String name, Pageable pageable);

    AdminEntity findByUserId(Long userId);

    UserService.SignInResult signIn(String account, String password);
    void signInLog(AdminEntity adminEntity);
    void signOut();
    ApiResult<Void> save(AdminDto adminDto);
}