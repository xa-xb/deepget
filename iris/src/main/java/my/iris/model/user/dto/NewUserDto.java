package my.iris.model.user.dto;

import my.iris.model.CloneAndSanitize;
import jakarta.validation.constraints.NotBlank;

public record NewUserDto(
        @NotBlank
        String username,

        @NotBlank
        String password,

        String captcha
) implements CloneAndSanitize<NewUserDto> {
    @Override
    public NewUserDto cloneAndSanitize() {
        return new NewUserDto(
                username,
                "******",
                captcha
        );
    }
}
