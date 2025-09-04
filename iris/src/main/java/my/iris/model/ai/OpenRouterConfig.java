package my.iris.model.ai;

import jakarta.validation.constraints.NotNull;
import my.iris.validation.groups.Add;
import my.iris.validation.groups.Edit;

public record OpenRouterConfig(
        @NotNull
        String apiUrl,
        @NotNull
        String apiKey
) {
}
