package my.iris.model.system.entity;

import my.iris.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Accessors(chain = true)
@Data
@EqualsAndHashCode(callSuper = false)

@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "t_system")
public class SystemEntity extends BaseEntity<SystemEntity> {
    private String entity;
    private String attribute;

    @Column(columnDefinition = "text")
    private String value;

    @Override
    protected SystemEntity self() {
        return this;
    }
}
