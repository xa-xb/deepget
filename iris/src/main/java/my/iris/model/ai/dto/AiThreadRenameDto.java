package my.iris.model.ai.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import my.iris.validation.UUIDv7;

@Getter
public class AiThreadRenameDto {
    @NotNull
    @UUIDv7
    String uuid;

    @NotNull
    @Size(min = 1, max = 50)
    String newName;

    public void setNewName(String newName) {
        if (newName != null) {
            this.newName = newName.trim();
        }
    }
}
