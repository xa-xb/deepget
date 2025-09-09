package my.iris.controller.api.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import my.iris.model.ApiResult;
import my.iris.model.QueryDto;
import my.iris.model.RsaPubKeyVo;
import my.iris.model.user.dto.NewUserDto;
import my.iris.model.user.dto.SignInDto;
import my.iris.model.user.vo.UserInfoVo;
import my.iris.service.email.EmailService;
import my.iris.service.system.SystemService;
import my.iris.service.user.UserService;
import my.iris.util.*;
import my.iris.util.session.Session;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Set;


/**
 * 用户接口
 */
@RestController
@RequestMapping(path = "/api/v1/main", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "user main controller", description = "No authentication required for this controller")
public class MainController {

    @Resource
    EmailService emailService;

    @Resource
    UserService userService;

    @Resource
    private SystemService systemService;

    /**
     * 验证码
     */
    @Operation(summary = "生成图片验证码", description = "生成一个新的图片验证码，并返回验证码图片")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功返回验证码图片", content = @Content(mediaType = "image/png"))
    })
    @GetMapping(value = "captcha")
    public void getCaptcha(HttpServletResponse response) {
        CaptchaUtils.writeResponse(response);
    }

    /**
     * 获取用户信息
     */
    @Operation(summary = "获取用户信息", description = "没有登录的 data:null")
    @GetMapping(value = "profile")
    public ApiResult<UserInfoVo> getProfile(HttpServletResponse response) {
        // 刷新客户端session过期时间
        var sid = TaskContext.getSession().getId(false);
        if (Objects.nonNull(sid)) {
            response.addHeader(HttpHeaders.SET_COOKIE, Session.newCookie(sid).toString());
        }

        var userToken = TaskContext.getUserToken();
        if (userToken != null) {
            var userInfo = new UserInfoVo(userToken.getUsername(), userToken.getEmail());
            return ApiResult.success(userInfo);
        }
        return ApiResult.success();
    }

    @Operation(summary = "reset password", description = """
            required fields: email, code, password(rsa encrypt required)
            """)
    @PostMapping("reset_password")
    public ApiResult<Void> resetPassword(@RequestBody @Validated QueryDto queryDto) {
        if (queryDto.email() == null) return ApiResult.badRequest("email is required");
        if (queryDto.code() == null) return ApiResult.badRequest("code is required");
        var password = SecurityUtils.rsaDecrypt(queryDto.password());
        var msg = ValidatorUtils.validatePassword(password);
        if (msg != null) return ApiResult.badRequest(msg);
        return userService.resetPasswordByEmailCode(queryDto.email(), queryDto.code(), password);
    }

    @Operation(summary = "send email code", description = """
            required fields: captcha(required for reset_password), email, name
            allowed names: bind_email, reset_password
            """)
    @PostMapping("send_email_code")
    public ApiResult<Void> sendEmailCode(@RequestBody @Validated QueryDto queryDto) {
        Set<String> allowedNames = Set.of("bind_email", "reset_password");
        var userToken = TaskContext.getUserToken();
        Long userId = userToken == null ? null : userToken.getUserId();
        if (queryDto.email() == null) return ApiResult.badRequest("email is required");
        if (queryDto.name() == null) return ApiResult.badRequest("name is required");
        if (!allowedNames.contains(queryDto.name()))
            return ApiResult.badRequest("Unsupported name: " + queryDto.name());

        switch (queryDto.name()) {
            case "bind_email" ->{
                if (userId == null) return ApiResult.unauthorized(null);
                var userEntity = userService.findUserByAccount(queryDto.email());
                if (userEntity != null && userEntity.getId().equals(userId)) {
                    return ApiResult.error("邮箱已绑定到当前账号，无需重复绑定！");
                }
            }
            case "reset_password" ->{
                var msg = ValidatorUtils.validateCaptcha(queryDto.captcha());
                if (msg != null) return ApiResult.badRequest(msg);
                CaptchaUtils.clear();
                var userEntity = userService.findUserByAccount(queryDto.email());
                if (userEntity == null) return ApiResult.badRequest("email not found");
            }
        }
        return emailService.sendVerificationCode(
                        userId,
                        queryDto.name(),
                        queryDto.email(),
                        Helper.randomNumber(6))
                .map(ApiResult::<Void>error)
                .orElseGet(ApiResult::success);
    }

    /**
     * administrator login
     *
     * @param query query
     * @return {}
     */
    @Operation(summary = "用户登录", description = "password需要RSA加密后发送")
    @PostMapping("sign_in")
    public ApiResult<UserService.SignInResult> signIn(@RequestBody @Validated SignInDto query) {
        var password = SecurityUtils.rsaDecrypt(query.getPassword());
        if (password == null) {
            return ApiResult.badRequest("密码错误，请发送加密后的密码数据");
        }
        var loginResult = userService.signIn(query.getAccount(), password);
        if (loginResult.getError() != null) {
            return ApiResult.error(loginResult.getError());
        }
        return ApiResult.success(loginResult);
    }


    @Operation(summary = "退出登录")
    @PostMapping("sign_out")
    public ApiResult<Void> signOut() {
        userService.signOut();
        return ApiResult.success();
    }

    @Operation(summary = "退出全部登录")
    @PostMapping("sign_out_all")
    public ApiResult<Void> signOutAll() {
        userService.signOutAll();
        return ApiResult.success();
    }

    @Operation(summary = "注册新用户", description = "password需要RSA加密后发送")
    @PostMapping("sign_up")
    public ApiResult<?> signUp(@RequestBody @Validated NewUserDto query) {
        var password = SecurityUtils.rsaDecrypt(query.password());
        if (password == null) {
            return ApiResult.badRequest("密码错误，请发送加密后的密码数据");
        }
        var msg = ValidatorUtils.validateCaptcha(query.captcha());
        if (msg != null) {
            return ApiResult.badRequest(msg);
        }
        msg = userService.signUp(query.username().toLowerCase(), password, true);
        if (msg != null) {
            return ApiResult.badRequest(msg);
        }
        return ApiResult.success();
    }

    /**
     * @return RSA public key
     */
    @Operation(summary = "RSA public key")
    @PostMapping("rsa_pub_key")
    public ApiResult<RsaPubKeyVo> getRsaPubKey() {
        return systemService.getRsaPubKey();

    }

}
