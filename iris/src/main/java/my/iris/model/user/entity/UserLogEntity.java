package my.iris.model.user.entity;

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
@Table(name = "t_user_log")
public class UserLogEntity extends BaseEntity<UserLogEntity> {
    private String ip;

    private Long userId;

    private String operation;

    @Column(columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private String content;

    private Integer duration;

    @Override
    protected UserLogEntity self() {
        return this;
    }
}
