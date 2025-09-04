package my.iris.repository.system;

import my.iris.model.system.entity.AdminLogEntity;
import my.iris.model.system.vo.AdminLogVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AdminLogRepository extends JpaRepository<AdminLogEntity, Long> {
    @Query("""
            SELECT NEW my.iris.model.system.vo.AdminLogVo(
                a.id, a.createdAt, a.userId, b.name, a.ip,
                a.operation, a.content, a.duration)
            FROM AdminLogEntity a
            LEFT JOIN UserEntity b ON a.userId = b.id
            WHERE (:name IS NULL OR b.name LIKE :name)
                AND (:ip IS NULL OR a.ip LIKE :ip)
                AND (:operation IS NULL OR a.operation LIKE :operation)
            """)
    Page<AdminLogVo> getPage(
            String name,
            String ip,
            String operation,
            Pageable pageable);
}
