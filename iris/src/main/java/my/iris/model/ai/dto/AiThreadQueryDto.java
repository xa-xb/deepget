package my.iris.model.ai.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import my.iris.model.PageDto;
import org.springframework.util.StringUtils;

@Getter
public class AiThreadQueryDto extends PageDto {
    @Min(0L)
    @Max(2L)
    @NotNull
    private Long status;
    private String title;
    private String username;

    public void setTitle(String title) {
        this.title = StringUtils.hasText(title) ? title.trim() : null;
    }

    public void setUsername(String username) {
        this.username = StringUtils.hasText(username) ? username.trim() : null;
    }
}
