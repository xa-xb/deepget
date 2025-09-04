package my.iris.repository.user;

import my.iris.model.user.entity.UserHotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserHotRepository extends JpaRepository<UserHotEntity, Long> {
}
