package my.iris.model.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import my.iris.model.PageDto;

@Getter
public class QueryUserDto extends PageDto {

    @Schema(description = "用户名")
    String name;

    @Schema(description = "手机号")
    String mobile;

    @Schema(description = "Email")
    String email;

    @Schema(description = "登录状态，1：全部, 2:有登录记录, 3:无登录记录", example = "1", minimum = "1", maximum = "3", defaultValue = "1")
    @Min(1)
    @Max(3)
    Integer signStatus;
}
