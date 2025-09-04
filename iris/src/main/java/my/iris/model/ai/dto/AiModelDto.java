package my.iris.model.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import my.iris.validation.groups.Add;
import my.iris.validation.groups.Edit;

import java.math.BigDecimal;

@Getter
public class AiModelDto {
    @NotNull(groups = Edit.class)
    @Null(groups = {Add.class})
    Long id;

    @NotBlank(groups = {Add.class, Edit.class})
    String name;

    @NotNull(groups = {Add.class, Edit.class})
    String sysName;

    @NotNull(groups = {Add.class, Edit.class})
    String openRouterName;

    @NotNull(groups = {Add.class, Edit.class})
    Boolean free;

    @NotNull(groups = {Add.class, Edit.class})
    Long providerId;

    @NotNull(groups = {Add.class, Edit.class})
    @Positive
    BigDecimal cnyPerToken;

    @NotNull(groups = {Add.class, Edit.class})
    Integer orderNum;

    @NotNull(groups = {Add.class, Edit.class})
    Boolean enabled;

    @NotNull(groups = {Add.class, Edit.class})
    String intro;

    public void setName(String name) {
        if (name != null) {
            this.name = name.trim();
        }
    }

    public void setSysName(String sysName) {
        if (sysName != null) {
            this.sysName = sysName.trim();
        }
    }

    public void setOpenRouterName(String openRouterName) {
        if (openRouterName != null) {
            this.openRouterName = openRouterName.trim();
        }
    }
}
