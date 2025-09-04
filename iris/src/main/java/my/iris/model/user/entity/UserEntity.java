package my.iris.model.user.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import my.iris.config.AppConfig;
import my.iris.model.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Accessors(chain = true)
@Data
@EqualsAndHashCode(callSuper = false)

@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "t_user")
public class UserEntity extends BaseEntity<UserEntity> {
    @Column(unique = true)
    private String name;

    private String avatar;
    private String email;
    private String mobile;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = AppConfig.DATE_TIME_FORMAT, timezone = AppConfig.TIME_ZONE)
    private LocalDateTime lastSignInAt;

    @Column(columnDefinition = "inet")
    @JdbcTypeCode(SqlTypes.INET)
    private String lastSignInIp;

    private Integer level;
    @JsonIgnore
    private String password;
    private String gender;
    private Long status;

    @Column(name = "created_ip", columnDefinition = "inet")
    @JdbcTypeCode(SqlTypes.INET)
    private String createdIp;

    private LocalDateTime deletedAt;

    @Override
    protected UserEntity self() {
        return this;
    }
}