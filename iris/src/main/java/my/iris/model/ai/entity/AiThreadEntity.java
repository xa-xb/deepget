package my.iris.model.ai.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import my.iris.model.BaseEntity;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.UUID;

@Accessors(chain = true)
@Data
@EqualsAndHashCode(callSuper = false)

@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "t_ai_thread")
public class AiThreadEntity extends BaseEntity<AiThreadEntity> {
    @ColumnDefault("NULL")
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column
    @NotBlank
    private String title;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column
    @NotNull
    private UUID uuid;

    @Override
    protected AiThreadEntity self() {
        return this;
    }
}
