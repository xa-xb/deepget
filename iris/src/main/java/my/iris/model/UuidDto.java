package my.iris.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import my.iris.validation.UUIDv7;

@Getter
public class UuidDto {
    @NotNull
    @UUIDv7
    String uuid;
}
