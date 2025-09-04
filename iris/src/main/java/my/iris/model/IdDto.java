package my.iris.model;

import jakarta.validation.constraints.NotNull;

/**
 * id data transfer object
 */

public record IdDto(
        @NotNull
        Long id
) {

}
