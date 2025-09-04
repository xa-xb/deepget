package my.iris.model.ai.entity;

import my.iris.model.BaseEntity;
import my.iris.validation.groups.Add;
import my.iris.validation.groups.Edit;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;


@Accessors(chain = true)
@Data
@EqualsAndHashCode(callSuper = false)

@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "t_token_log")
public class TokenLogEntity extends BaseEntity<TokenLogEntity> {
    @NotNull(groups = {Add.class, Edit.class})
    private Long uid;

    @Digits(integer = 12, fraction = 2)
    @NotNull(groups = {Add.class, Edit.class})
    private BigDecimal amount;
    private BigDecimal token;
    @NotBlank(groups = {Add.class, Edit.class})
    private String note;

    @Override
    protected TokenLogEntity self() {
        return this;
    }
}
