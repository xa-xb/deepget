package my.iris.controller.admin.devel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import my.iris.auth.Authorize;
import my.iris.model.ApiResult;
import my.iris.model.QueryDto;
import my.iris.util.SecurityUtils;
import my.iris.util.ValidatorUtils;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/admin/devel/security", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "admin development security", description = "development security controller")
public class SecurityController {

    @Resource
    PasswordEncoder passwordEncoder;

    @Authorize("/devel/security/query")
    @PostMapping("encode_password")
    @Operation(summary = "encode password", description = "field used: password(rsa encrypt required)")
    public ApiResult<String> password(@RequestBody @Validated QueryDto queryDto) {
        var password = SecurityUtils.rsaDecrypt(queryDto.password());
        var msg = ValidatorUtils.validatePassword(password);
        if (msg != null) return ApiResult.badRequest(msg);
        return ApiResult.success("", passwordEncoder.encode(password));
    }
}
