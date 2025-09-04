package my.iris.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;

@Getter
public class PageDto {
    @Schema(description = "当前页", example = "1", minimum = "1", defaultValue = "1")
    @Min(1)
    Integer page = 1;

    @Schema(description = "每页显示的记录数", example = "20", maximum = "200", minimum = "1", defaultValue = "20")
    @Max(200)
    Integer pageSize = 200;

    @Schema(description = "排序字段", example = "id,desc")
    String sort;
}
