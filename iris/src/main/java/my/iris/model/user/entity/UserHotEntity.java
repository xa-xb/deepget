package my.iris.model.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Accessors(chain = true)
@Data

@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "t_user_hot")
public class UserHotEntity {
    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    private Long credits;
}
