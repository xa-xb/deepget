package my.iris.model.ai.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import my.iris.model.CloneAndSanitize;
import my.iris.model.ai.OpenRouterConfig;
import my.iris.util.JsonUtils;


@Data
public class AiConfigDto implements CloneAndSanitize<AiConfigDto> {
    @NotNull
    @Min(0)
    Long memoryCount;

    @NotNull
    OpenRouterConfig openRouterConfig;

    @Override
    public AiConfigDto cloneAndSanitize() {
        var obj = JsonUtils.clone(this);
        obj.openRouterConfig = new OpenRouterConfig(obj.openRouterConfig.apiUrl(), "***");
        return obj;
    }
}
