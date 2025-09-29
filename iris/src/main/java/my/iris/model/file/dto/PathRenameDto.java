package my.iris.model.file.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PathRenameDto {
    @NotBlank
    String newPath;
    @NotBlank
    String oldPath;
}