package my.iris.repository.user;

import my.iris.model.user.entity.UserLogEntity;
import my.iris.model.user.vo.UserLogVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLogRepository extends JpaRepository<UserLogEntity, Long> {
    @Query("""
            SELECT NEW my.iris.model.user.vo.UserLogVo(
                a.id, a.createdAt, a.userId, b.name, a.ip,
                a.operation, a.content, a.duration)
            FROM UserLogEntity a
            LEFT JOIN UserEntity b on a.userId = b.id
            WHERE (:name IS NULL OR b.name LIKE :name)
                AND (:ip IS NULL OR a.ip LIKE :ip)
                AND (:operation IS NULL OR a.operation LIKE :operation)
            """)
    Page<UserLogVo> getPage(
            String name,
            String ip,
            String operation,
            Pageable pageable);

}
