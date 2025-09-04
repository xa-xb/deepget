package my.iris.model.system.entity;


import my.iris.model.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import my.iris.config.AppConfig;
import my.iris.validation.groups.Edit;

import java.sql.Timestamp;

@Accessors(chain = true)
@Data
@EqualsAndHashCode(callSuper = false)

@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "t_payment")
public class PaymentEntity extends BaseEntity<PaymentEntity> {
    @Column(columnDefinition = "text")
    @JsonIgnore
    private String config;

    @NotNull(groups = {Edit.class})
    private Boolean enable;

    @NotBlank(groups = {Edit.class})
    private String name;
    private String nameCn;
    @NotNull(groups = {Edit.class})
    private Integer orderNum;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = AppConfig.DATE_TIME_FORMAT, timezone = AppConfig.TIME_ZONE)
    @Null(groups = {Edit.class})
    private Timestamp updateTime;

    @Override
    protected PaymentEntity self() {
        return this;
    }
}
