package my.iris.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import my.iris.validation.groups.Add;
import my.iris.validation.groups.Edit;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

/**
 * base entity
 */
@Getter
@MappedSuperclass
public abstract class BaseEntity<T extends BaseEntity<T>> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "bigint")
    @NotNull(groups = Edit.class)
    @Null(groups = Add.class)
    private Long id;

    @ColumnDefault("NOW()")
    @Column(name = "created_at", nullable = false, columnDefinition = "timestamp(0) default now()", updatable = false)
    @Schema(description = "添加时间", example = "2025-01-01 08:00:00")
    private LocalDateTime createdAt;

    public T setId(Long id) {
        this.id = id;
        return self();
    }

    public T setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return self();
    }

    // 自引用方法，返回子类实例
    protected abstract T self();

}
