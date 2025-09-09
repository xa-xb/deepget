package my.iris.model.email.vo;

import java.time.LocalDateTime;

public record EmailLogVo(
        Long id,
        String from,
        String to,
        String action,
        Integer duration,
        LocalDateTime createdAt
) {
}
