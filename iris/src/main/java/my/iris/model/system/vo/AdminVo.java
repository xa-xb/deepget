package my.iris.model.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.net.InetAddress;
import java.sql.Timestamp;


public record AdminVo(
        Long id,
        Long userId,
        Boolean enabled,
        String name,
        String email,
        String mobile,
        String trueName,
        Long[] roleIds,
        String roleNames,
        @Schema(description = "最近活动时间", example = "2025-01-01 08:00:00")
        Timestamp lastActionAt,
        @Schema(description = "最近登录时间", example = "2025-01-01 08:00:00")
        Timestamp lastSignInAt,
        @Schema(description = "最近登录IP", example = "::1")
        InetAddress lastSignInIp,
        @Schema(description = "注册时间", example = "2025-01-01 08:00:00")
        Timestamp createdAt,
        @Schema(description = "注册IP", example = "::1")
        InetAddress createdIp
) {
}
