package my.iris.repository.email;

import my.iris.model.email.entity.EmailLogEntity;
import my.iris.model.email.vo.EmailLogVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    @Query("""
            SELECT NEW my.iris.model.email.vo.EmailLogVo(
                a.id, a.from, a.to, a.action, a.duration,
                a.createdAt)
            FROM EmailLogEntity a
            WHERE (:action IS NULL OR a.action like :action)
                AND (:from IS NULL OR a.from like :from)
                AND (:to IS NULL OR a.to like :to)
            """)
    Page<EmailLogVo> getPage(Pageable pageable, String action, String from, String to);
}
