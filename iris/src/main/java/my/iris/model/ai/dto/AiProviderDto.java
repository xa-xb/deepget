package my.iris.model.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import my.iris.model.CloneAndSanitize;
import my.iris.util.JsonUtils;
import my.iris.validation.groups.Add;
import my.iris.validation.groups.Edit;

@Getter
public class AiProviderDto implements CloneAndSanitize<AiProviderDto> {
    @Null(groups = {Add.class})
    @NotNull(groups = {Edit.class})
    Long id;

    @NotBlank(groups = {Add.class, Edit.class})
    String name;

    @NotNull(groups = {Add.class, Edit.class})
    @Schema(description = "图标,svg内容", example = "<svg>...</svg>")
    String iconSvg;

    @Schema(description = "api协议", example = "OPEN_AI")
    @NotBlank(groups = {Add.class, Edit.class})
    String apiCompatible;

    @NotBlank(groups = {Add.class, Edit.class})
    String url;

    @NotNull(groups = {Add.class, Edit.class})
    String apiUrl;

    @NotNull(groups = {Add.class, Edit.class})
    String apiKey;

    @NotNull(groups = {Add.class, Edit.class})
    Boolean openRouter;

    @NotNull(groups = {Add.class, Edit.class})
    Integer orderNum;

    public void setName(String name) {
        if (name != null) {
            this.name = name.trim();
        }
    }

    public void setIconSvg(String iconSvg) {
        if (iconSvg != null) {
            this.iconSvg = iconSvg.trim();
        }
    }

    public void setApiKey(String apiKey) {
        if (apiKey != null) {
            this.apiKey = apiKey.trim();
        }
    }

    public void setApiUrl(String apiUrl) {
        if (apiUrl != null) {
            this.apiUrl = apiUrl.trim();
        }
    }

    public void setUrl(String url) {
        if (url != null) {
            this.url = url.trim();
        }
    }

    @Override
    public AiProviderDto cloneAndSanitize() {
        var obj = JsonUtils.clone(this);
        obj.apiKey = "******";
        return obj;
    }
}
