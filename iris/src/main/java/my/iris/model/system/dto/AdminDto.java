package my.iris.model.system.dto;

import my.iris.validation.groups.Add;
import my.iris.validation.groups.Edit;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AdminDto(
        @NotNull(groups = {Edit.class})
        Long id,

        @Schema(description = "是否启用", example = "true")
        @NotNull(groups = {Add.class, Edit.class})
        Boolean enabled,

        @Schema(description = "用户账号")
        @NotBlank(groups = {Add.class})
        String account,

        @NotNull(groups = {Edit.class})
        Long userId,

        @Schema(description = "角色id", example = "[1,2]")
        @NotNull(groups = {Add.class, Edit.class})
        List<Long> roleIds,


        @Schema(description = "真实姓名", example = "张三")
        String trueName
) {
}
