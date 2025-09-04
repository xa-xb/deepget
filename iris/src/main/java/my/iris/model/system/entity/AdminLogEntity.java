package my.iris.model.system.entity;

import my.iris.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Accessors(chain = true)
@Data
@EqualsAndHashCode(callSuper = false)

@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "t_admin_log")
public class AdminLogEntity extends BaseEntity<AdminLogEntity> {
    private String ip;

    private Long userId;
    private String operation;

    @Column(columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private String content;

    private Integer duration;

    @Override
    protected AdminLogEntity self() {
        return this;
    }
}
