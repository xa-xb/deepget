package my.iris.controller.admin.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import my.iris.auth.Authorize;
import my.iris.model.ApiResult;
import my.iris.model.IdDto;
import my.iris.model.PageVo;
import my.iris.model.QueryDto;
import my.iris.model.user.dto.NewUserDto;
import my.iris.model.user.dto.QueryUserDto;
import my.iris.model.user.vo.UserVo;
import my.iris.service.system.AdminLogService;
import my.iris.service.user.UserService;
import my.iris.util.DbUtils;
import my.iris.util.SecurityUtils;
import my.iris.util.ValidatorUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(path = "/admin/user/user", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "admin user", description = "用户管理")
public class UserController {
    @Resource
    UserService userService;
    @Resource
    private AdminLogService adminLogService;

    @Authorize("/user/user/add")
    @PostMapping("add")
    @Operation(summary = "新建用户, password需要RSA加密")
    public ApiResult<Void> add(@RequestBody @Validated NewUserDto query) {
        var password = SecurityUtils.rsaDecrypt(query.password());
        if (password == null) {
            return ApiResult.badRequest("密码解密失败");
        }
        return userService.signUpByAdmin(query.username().toLowerCase(), password);
    }

    @Authorize("/user/user/query")
    @PostMapping("get")
    public ApiResult<UserVo> get(@RequestBody @Validated IdDto idDto) {
        var entity = userService.getUserVoById(idDto.id());
        if (entity == null) {
            return ApiResult.error("用户不存在, id:" + idDto.id());
        }
        return ApiResult.success(entity);
    }


    @Authorize("/user/user/query")
    @Operation(summary = "获取用户列表", description = """
            sort可选值: id, credits, lastSignInAt, name
            """)
    @PostMapping("list")
    public ApiResult<PageVo<UserVo>> list(@RequestBody QueryUserDto query) {
        String[] sortableColumns = new String[]{
                "credits:b.credits", "id", "lastSignInAt", "name"
        };

        return ApiResult.success(PageVo.of(userService.getPage(
                query.getName(),
                query.getMobile(),
                query.getEmail(),
                query.getSignStatus(),
                PageRequest.of(
                        query.getPage() - 1,
                        query.getPageSize(),
                        DbUtils.getSort(sortableColumns, query.getSort(), "id,desc")
                )
        )));
    }

    @Authorize("/user/user/changePassword")
    @PostMapping("changePassword")
    public ApiResult<Void> changePassword(@RequestBody QueryDto query) {
        long userId = query.id();
        String pwd = query.password();
        pwd = SecurityUtils.rsaDecrypt(pwd);
        String msg = ValidatorUtils.validatePassword(pwd, "密码");
        if (msg != null) return ApiResult.badRequest(msg);
        msg = userService.changePassword(userId, pwd);
        if (msg == null)  {
            adminLogService.addLog("change_user_password", Map.of("userId", userId));
            return ApiResult.success();
        }
        return ApiResult.error(msg);
    }

}
