package my.iris.model.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import my.iris.validation.UUIDv7;

@Getter
public class AiChatDto {

    @UUIDv7
    private String threadUuid;

    @NotBlank
    private String prompt;

    @NotNull
    private Long modelId;

    public void setPrompt(String prompt) {
        if (prompt != null) {
            this.prompt = prompt.trim();
        }
    }
}
