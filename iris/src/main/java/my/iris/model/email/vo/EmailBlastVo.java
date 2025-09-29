package my.iris.model.email.vo;

import java.time.LocalDateTime;

public record EmailBlastVo(
        String id,
        String to,
        String sender,
        Boolean force,
        LocalDateTime createdAt
) {
    public EmailBlastVo withId(String id) {
        return new EmailBlastVo(id, to, sender, force, createdAt);
    }
}
