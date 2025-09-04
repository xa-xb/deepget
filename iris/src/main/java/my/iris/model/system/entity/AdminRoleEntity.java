package my.iris.model.system.entity;

import my.iris.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.hibernate.validator.constraints.Range;
import my.iris.validation.groups.Add;
import my.iris.validation.groups.Edit;

import java.util.List;

@Accessors(chain = true)
@Data
@EqualsAndHashCode(callSuper = false)

@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "t_admin_role")
public class AdminRoleEntity extends BaseEntity<AdminRoleEntity> {
    @NotEmpty(groups = {Add.class, Edit.class})
    private String name;

    @NotNull(groups = {Add.class, Edit.class})
    @Range(min = 1, max = 999,groups = {Add.class, Edit.class})
    private Integer orderNum;

    @NotNull(groups = {Add.class, Edit.class})
    @JdbcTypeCode(SqlTypes.ARRAY)
    private List<Long> authIds;

    @NotNull(groups = {Add.class, Edit.class})
    private Boolean enabled;

    @Override
    protected AdminRoleEntity self() {
        return this;
    }
}