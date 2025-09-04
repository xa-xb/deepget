package my.iris.repository.ai;

import jakarta.persistence.LockModeType;
import my.iris.model.ai.entity.AiThreadEntity;
import my.iris.model.ai.vo.AiChatThreadVo;
import my.iris.model.ai.vo.AiThreadClientVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AiChatThreadRepository extends JpaRepository<AiThreadEntity, Long> {

    @Modifying
    @Query("""
            UPDATE AiThreadEntity a
            SET a.deletedAt = current_timestamp
            WHERE a.userId = :userId
                AND a.deletedAt IS NULL
            """)
    void deleteAllByUserId(Long userId);

    @Query("""
            SELECT a
            FROM AiThreadEntity a
            WHERE  a.uuid = :uuid
                AND a.userId = :userId
                AND a.deletedAt IS NULL
            """)
    Optional<AiThreadEntity> getClientThread(UUID uuid, Long userId);

    @Query("""
            SELECT NEW my.iris.model.ai.vo.AiChatThreadVo(
                a.id, a.userId, b.name, a.title, a.createdAt
            )
            FROM AiThreadEntity  a
            LEFT JOIN UserEntity b ON a.userId = b.id
            WHERE (:status = 0 OR (:status = 1 AND a.deletedAt IS NULL) OR (:status = 2 AND a.deletedAt IS NOT NULL))
                AND (:title IS NULL OR a.title like :title)
                AND (:username IS NULL OR b.name = :username)
            """)
    Page<AiChatThreadVo> getPage(Pageable pageable, Long status, String title, String username);

    @Query("""
            SELECT new my.iris.model.ai.vo.AiThreadClientVo(
                a.uuid, a.title, a.createdAt)
            FROM AiThreadEntity a
            WHERE a.userId = ?1 AND a.deletedAt IS NULL
            ORDER BY a.id DESC
            """)
    List<AiThreadClientVo> listClientThreads(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM AiThreadEntity a WHERE a.uuid = ?1")
    Optional<AiThreadEntity> findByUuidForUpdate(UUID uuid);

    Optional<AiThreadEntity> findByUuidAndUserId(UUID uuid, Long userId);

}
