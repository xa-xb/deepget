package my.iris.model.email.dto;

import lombok.Getter;
import my.iris.model.PageDto;
import org.springframework.util.StringUtils;

@Getter
public class EmailQueryDto extends PageDto {
    String action;
    String from;
    String to;

    public void setAction(String action) {
        if (StringUtils.hasText(action)) this.action = action.trim();
    }

    public void setFrom(String from) {
        if (StringUtils.hasText(from)) this.from = from.trim();
    }

    public void setTo(String to) {
        if (StringUtils.hasText(to)) this.to = to.trim();
    }
}
