package my.iris.model;

import jakarta.validation.constraints.NotNull;

public record IdStrDto(
        @NotNull
        String id
) {
}
