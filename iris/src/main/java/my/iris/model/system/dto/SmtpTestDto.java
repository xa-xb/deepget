package my.iris.model.system.dto;

import my.iris.model.CloneAndSanitize;
import my.iris.util.JsonUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SmtpTestDto(
        @Email
        @NotBlank
        String to,

        @NotNull
        @Valid
        List<SmtpServerDto> smtpServers
) implements CloneAndSanitize<SmtpTestDto> {
    @Override
    public SmtpTestDto cloneAndSanitize() {
        var clone = JsonUtils.clone(this);
        for (var smtpServerDto : clone.smtpServers) {
            smtpServerDto.setPassword("******");
        }
        return clone;
    }
}