package my.iris.model.system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public  class SmtpServerDto {
    @NotNull
    private Boolean enable;

    @Email
    @NotBlank
    private String name;

    @NotBlank
    private String password;

    @NotBlank
    private String host;
    @NotNull
    private Integer port;
    @NotNull
    private Boolean starttls;
}