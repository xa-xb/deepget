package my.iris.model.system.entity;

import my.iris.model.BaseEntity;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
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

import java.time.LocalDateTime;
import java.util.List;

@Accessors(chain = true)
@Data
@EqualsAndHashCode(callSuper = false)

@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "t_admin")
public class AdminEntity extends BaseEntity<AdminEntity> {
    @Hidden
    private Long userId;

    @Schema(description = "是否启用", example = "true")
    private Boolean enabled;

    @Schema(description = "角色id", example = "[1,2]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private List<Long> roleIds;

    @Schema(description = "真实姓名", example = "张三")
    private String trueName;

    private LocalDateTime lastActionAt;

    private LocalDateTime lastSignInAt;

    @Column(columnDefinition = "inet")
    @JdbcTypeCode(SqlTypes.INET)
    private String lastSignInIp;

    @Column(columnDefinition = "inet")
    @JdbcTypeCode(SqlTypes.INET)
    private String createdIp;

    @Override
    protected AdminEntity self() {
        return this;
    }
}