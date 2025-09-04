package my.iris.model.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.net.InetAddress;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public record UserLogVo(
        Long id,

        @Schema(description = "时间", example = "2025-01-01 08:00:00")
        LocalDateTime createdAt,

        Long uid, String name,

        @Schema(description = "IP address", example = "::1", type = "string")
        String ip,

        String operation,
        String content,
        Integer duration
) {
}
