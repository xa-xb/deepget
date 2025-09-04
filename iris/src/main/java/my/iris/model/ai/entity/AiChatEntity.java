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
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.UUID;

@Accessors(chain = true)
@Data
@EqualsAndHashCode(callSuper = false)

@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "t_ai_chat")
public class AiChatEntity extends BaseEntity<AiChatEntity> {

    @NotNull
    @Column(name = "thread_id", nullable = false)
    private Long threadId;

    @NotNull
    @Column(name = "model_id", nullable = false)
    private Long modelId;

    @NotBlank
    @Column(nullable = false, length = Integer.MAX_VALUE)
    private String prompt;

    @NotNull
    @Column(name = "input_token_count", nullable = false)
    private Long inputTokenCount;

    @NotNull
    @Column(name = "output_token_count", nullable = false)
    private Long outputTokenCount;

    @NotNull
    @Column(name = "total_token_count", nullable = false)
    private Long totalTokenCount;

    @Column
    private String completion;

    @NotNull
    @Column(nullable = false)
    private Long creditsUsed;

    @NotNull
    @Column(nullable = false)
    private Long creditsRemaining;

    @NotNull
    @Column(name = "duration", nullable = false)
    private Integer duration;

    @Column
    @NotNull
    private UUID uuid;

    @Column
    private String error;

    @Override
    protected AiChatEntity self() {
        return this;
    }
}
