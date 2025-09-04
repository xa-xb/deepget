package my.iris.model.ai.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import my.iris.model.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * ai provider
 */

@Accessors(chain = true)
@Data
@EqualsAndHashCode(callSuper = false)

@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "t_ai_provider")
public class AiProviderEntity extends BaseEntity<AiProviderEntity> {

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    private String iconSvg;

    // 接口协议
    @NotNull
    @Column(name = "api_compatible", nullable = false)
    private String apiCompatible;

    @Size(max = 255)
    @NotNull
    @Column(name = "url")
    @Schema(description = "url", example = "https://example.com")
    private String url;

    @NotNull
    @Size(max = 255)
    private String apiUrl;

    // AES加密保存
    @NotNull
    private String apiKey;

    @NotNull
    private Boolean openRouter;

    @NotNull
    private Integer orderNum;


    @Override
    protected AiProviderEntity self() {
        return this;
    }
}
