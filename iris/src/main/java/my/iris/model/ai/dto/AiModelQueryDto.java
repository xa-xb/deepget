package my.iris.model.ai.dto;

import lombok.Getter;
import my.iris.model.PageDto;

@Getter
public class AiModelQueryDto extends PageDto {
    String name;
    Long providerId;
    Boolean enabled;
}
