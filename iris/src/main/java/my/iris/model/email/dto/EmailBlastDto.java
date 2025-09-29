package my.iris.model.email.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record EmailBlastDto(
        @NotEmpty
        List<@Email String> emails,

        @Schema(description = "Force send, bypass duplicate check")
        Boolean force,

        @Email
        String sender
) {
    public EmailBlastDto {
        if (force == null) {
            force = false;
        }
    }
}
