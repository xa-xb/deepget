package my.iris.model.system.entity;

import my.iris.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.UUID;

@Accessors(chain = true)
@Data
@EqualsAndHashCode(callSuper = false)

@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "t_email_log")
public class EmailLogEntity extends BaseEntity<EmailLogEntity> {

    @NotNull
    @Column(name = "uuid", nullable = false)
    private UUID uuid;

    @Column(name = "user_id")
    private Long userId;

    @Size(max = 255)
    @NotNull
    private String ip;

    @Size(max = 255)
    @NotNull
    @Column(name = "\"from\"", nullable = false)
    private String from;


    @Size(max = 255)
    @NotNull
    @Column(name = "\"to\"", nullable = false)
    private String to;

    @NotNull
    @Column(nullable = false)
    private String action;

    @Column(name = "data")
    private String data;


    @Column(name = "err")
    private String err;

    @NotNull
    @Column(name = "duration", nullable = false)
    private Integer duration;

    @Override
    protected EmailLogEntity self() {
        return this;
    }
}