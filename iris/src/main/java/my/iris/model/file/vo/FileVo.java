package my.iris.model.file.vo;

import java.time.LocalDateTime;

public record FileVo(
        Boolean isDirectory,
        String name,
        Long size,
        LocalDateTime modifiedAt
) {
}
