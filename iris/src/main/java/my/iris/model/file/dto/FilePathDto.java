package my.iris.model.file.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class FilePathDto {
    @NotBlank
    String filePath;
}
