package my.iris.model.ai.dto;

import lombok.Getter;
import my.iris.model.PageDto;
import org.springframework.util.StringUtils;

@Getter
public class AiChatQueryDto extends PageDto {
    private Long threadId;
    private String username;

    public void setUsername(String username) {
        this.username = StringUtils.hasText(username) ? username.trim() : null;
    }
}
