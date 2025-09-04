package my.iris.model.system.entity;

import my.iris.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import my.iris.validation.groups.Add;
import my.iris.validation.groups.Edit;

@Accessors(chain = true)
@Data
@EqualsAndHashCode(callSuper = false)

@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "t_admin_menu")
public class AdminMenuEntity extends BaseEntity<AdminMenuEntity> {
    private Boolean cache;
    private String page;

    @NotNull(groups = {Add.class, Edit.class})
    private Boolean enable;
    @NotNull(groups = {Add.class, Edit.class})
    private Boolean visible;

    private String icon;

    @NotBlank(groups = {Add.class, Edit.class})
    private String name;

    private Integer orderNum;

    @NotNull(groups = {Add.class, Edit.class})
    private Long parentId;

    @Null(groups = {Add.class, Edit.class})
    @Transient
    private String fullRoute;

    @NotBlank(groups = {Add.class, Edit.class})
    private String route;

    @Min(value = 1, groups = {Add.class, Edit.class})
    @Max(value = 3, groups = {Add.class, Edit.class})
    @NotNull(groups = {Add.class, Edit.class})
    private Integer type;

    @Override
    protected AdminMenuEntity self() {
        return this;
    }
}