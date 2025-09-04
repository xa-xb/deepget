package my.iris.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

public record PageVo<T>(
        @Schema(description = "当前页", example = "1")
        long currentPage,

        @Schema(description = "总页数", example = "10")
        long totalPages,

        @Schema(description = "总记录数", example = "200")
        long totalRecords,

        @Schema(description = "结果集")
        List<T> rows
) implements PageableVo<T> {
    public static <T> PageVo<T> of(Page<T> page) {
        return new PageVo<>(
                page.getNumber() + 1,
                page.getTotalPages(),
                page.getTotalElements(),
                page.getContent());
    }
}
