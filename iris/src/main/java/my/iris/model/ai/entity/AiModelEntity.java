package my.iris.model.ai.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import my.iris.model.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;

@Accessors(chain = true)
@Data
@EqualsAndHashCode(callSuper = false)

@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "t_ai_model")
public class AiModelEntity extends BaseEntity<AiModelEntity> {
    @NotNull
    @OneToOne
    @JoinColumn(name = "provider_id")
    AiProviderEntity provider;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 255)
    @NotNull
    @Column(name = "sys_name", nullable = false)
    private String sysName;

    @Size(max = 255)
    @NotNull
    @Column(name = "open_router_name", nullable = false)
    private String openRouterName;
    
    @NotNull
    private Boolean free;

    @NotNull
    private BigDecimal cnyPerToken;

    @NotNull
    @Size(max = 255)
    private String intro;

    @NotNull
    private Boolean enabled;

    @NotNull
    private Integer orderNum;

    @Override
    protected AiModelEntity self() {
        return this;
    }

    public AiModelEntity setName(String name) {
        if (name != null) {
            this.name = name.trim();
        } else {
            this.name = null;
        }
        return this;
    }

    public AiModelEntity setSysName(String sysName) {
        if (sysName != null) {
            this.sysName = sysName.trim();
        } else {
            this.sysName = null;
        }
        return this;
    }
}
