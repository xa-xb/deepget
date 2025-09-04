package my.iris.model.devel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import my.iris.validation.groups.Add;

public record GenDto(
        @NotBlank(groups = {Add.class})
        @Pattern(groups = {Add.class}, regexp = "^[a-zA-Z][a-zA-Z0-9]*$", message = "业务名称只能包含字母、数字，必须字母开头")
        String businessName,

        @NotBlank(groups = {Add.class})
        String frontendPath,

        @NotBlank(groups = {Add.class})
        @Pattern(groups = {Add.class}, regexp = "^\\S+$", message = "菜单名称不能有空白字符")
        String menuName,

        @Positive(groups = {Add.class})
        Long moduleId,

        @NotBlank(groups = {Add.class})
        String captcha
) {
}
