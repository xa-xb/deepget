package my.iris.model.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;

import java.net.InetAddress;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public record UserVo(
        Long id,
        String name,
        String mobile,
        String email,
        String gender,
        Long credits,

        @Schema(description = "最近登录时间", example = "2025-01-01 08:00:00")
        LocalDateTime lastSignInAt,

        @Schema(description = "最近登录IP", example = "::1")
        String lastSignInIp,

        @Schema(description = "注册时间", example = "2025-01-01 08:00:00")
        LocalDateTime createdAt,

        @Schema(description = "注册IP", example = "::1")
        String createdIp
) {
}
