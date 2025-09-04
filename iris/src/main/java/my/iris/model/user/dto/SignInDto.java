package my.iris.model.user.dto;

import lombok.Getter;
import my.iris.model.CloneAndSanitize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import my.iris.util.JsonUtils;

@Getter
public class SignInDto implements CloneAndSanitize<SignInDto>{
        @Schema(description = "登录账号，用户名/邮箱/手机号")
        @NotBlank
        String account;

        @Schema(description = "密码")
        @NotBlank
        String password;

    public void setAccount(String account) {
        if (account != null) {
            this.account = account.trim().toLowerCase();
        }
    }

    @Override
    public SignInDto cloneAndSanitize() {
        var clone = JsonUtils.clone(this);
        clone.password = "******";
        return clone;
    }
}
