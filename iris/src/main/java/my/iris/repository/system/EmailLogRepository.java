package my.iris.repository.system;

import my.iris.model.system.entity.EmailLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EmailLogRepository extends JpaRepository<EmailLogEntity, Long> {
    @NativeQuery("""
            SELECT COUNT(*)
            FROM t_email_log
            WHERE "to" = :email
                AND created_at >= CURRENT_DATE
            
            """)
    Long countByEmailToday(String email);

    // has error, don't use, test only
    @Query("""
            SELECT COUNT(a)
            FROM EmailLogEntity a
            WHERE (:email IS NULL OR a.to = :email)
                AND (:userId IS NULL OR a.userId = :userId)
                AND (:startDate IS NULL OR a.createdAt >= :startDate)
                AND (:endDate IS NULL OR a.createdAt <= :endDate)
            """)
    Long count(String email, Long userId, LocalDateTime startDate, LocalDateTime endDate);

    @NativeQuery(value = """
            SELECT *
            FROM t_email_log
            WHERE created_at >= NOW() - INTERVAL '5 minutes'
              AND "to" = :email
              AND action = :action
            ORDER BY created_at ASC
            LIMIT 1
            """)
    Optional<EmailLogEntity> findLatestWithin5Minutes(String email, String action);


    @NativeQuery("""
            select last_value from t_email_log_id_seq
            """)
    Long getLastId();
}
