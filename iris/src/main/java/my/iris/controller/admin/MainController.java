package my.iris.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.experimental.Accessors;
import my.iris.auth.Authorize;
import my.iris.cache.AdminMenuCache;
import my.iris.model.ApiResult;
import my.iris.model.RsaPubKeyVo;
import my.iris.model.system.entity.AdminEntity;
import my.iris.model.user.dto.SignInDto;
import my.iris.model.user.vo.SignInVo;
import my.iris.repository.system.AdminRepository;
import my.iris.service.system.AdminMenuService;
import my.iris.service.system.AdminRoleService;
import my.iris.service.system.AdminService;
import my.iris.service.system.SystemService;
import my.iris.util.CaptchaUtils;
import my.iris.util.SecurityUtils;
import my.iris.util.TaskContext;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * admin api
 * 管理员接口
 */
@RestController("adminMainController")
@RequestMapping(path = "/admin/main", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "admin main", description = "admin main controller, no authentication required for this controller")
public class MainController {

    @Resource
    AdminMenuCache adminMenuCache;
    @Resource
    AdminMenuService adminMenuService;

    @Resource
    AdminRoleService adminRoleService;

    @Resource
    AdminRepository adminRepository;

    @Resource
    AdminService adminService;

    @Resource
    private SystemService systemService;

    @Authorize(skipAuth = true)
    @Operation(summary = "生成图片验证码", description = "生成一个新的图片验证码，并返回验证码图片")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功返回验证码图片", content = @Content(mediaType = "image/png"))
    })
    @GetMapping(value = "captcha")
    public void getCaptcha(HttpServletResponse response) {
        CaptchaUtils.writeResponse(response);
    }


    /**
     * get user info
     *
     * @return user info , menu, permission
     */
    @Authorize(skipAuth = true)
    @Operation(summary = "获取当前管理员信息, 无需登录")
    @PostMapping("profile")
    public ApiResult<AdminInfoVo> getProfile() {
        var userToken = TaskContext.getUserToken();
        if (userToken == null) {
            return ApiResult.success();
        }
        AdminEntity adminEntity = adminRepository.findByUserId(userToken.getUserId()).orElse(null);
        if (adminEntity == null || !adminEntity.getEnabled()) {
            return ApiResult.success();
        }
        var ruleIdList = adminEntity.getRoleIds();
        var authorizeIds = adminRoleService.getAuthorizeIdsByRolesId(ruleIdList);
        boolean superAdmin = adminEntity.getRoleIds().contains(1L);
        var authorizeNames = adminMenuCache.getEnabledAuthorizeByIds(authorizeIds);
        return ApiResult.success(
                new AdminInfoVo()
                        .setAuthorize(authorizeNames)
                        .setEmail(userToken.getEmail())
                        .setName(userToken.getUsername())
                        .setMenu(adminMenuService.getMenu(authorizeNames, superAdmin))
                        .setSuperAdmin(superAdmin)
        );
    }

    /**
     * administrator login
     *
     * @param query query
     * @return {}
     */
    @Authorize(skipAuth = true)
    @Operation(summary = "管理员登录, password需要RSA加密")
    @PostMapping("sign_in")
    public ApiResult<SignInVo> signIn(@RequestBody @Validated SignInDto query) {

        var password = SecurityUtils.rsaDecrypt(query.getPassword());
        var loginResult = adminService.signIn(query.getAccount(), password);
        if (loginResult.getError() != null) {
            return ApiResult.error(loginResult.getError());
        }
        var signInVo = new SignInVo(loginResult.getUserEntity().getName(),
                loginResult.getUserEntity().getEmail());
        return ApiResult.success(signInVo);
    }


    @Authorize(skipAuthorization = true)
    @Operation(summary = "管理员退出登录, 无需鉴权")
    @PostMapping("sign_out")
    public ApiResult<Void> signOut() {
        adminService.signOut();
        return ApiResult.success();
    }

    /**
     * @return RSA public key
     */
    @Authorize(skipAuth = true)
    @Operation(summary = "rsa public key, not need login")
    @PostMapping("rsa_pub_key")
    public ApiResult<RsaPubKeyVo> getRsaPubKey() {
        return systemService.getRsaPubKey();
    }

    @Accessors(chain = true)
    @Data
    public static class AdminInfoVo {
        private String[] authorize;
        private String email;
        private String name;
        private List<AdminMenuService.Menu> menu;
        private Boolean superAdmin;
    }
}
