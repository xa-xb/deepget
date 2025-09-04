package my.iris.repository.ai;

import my.iris.model.ai.entity.AiChatEntity;
import my.iris.model.ai.vo.AiChatAdminVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface AiChatRepository extends JpaRepository<AiChatEntity, Long> {
    @Query("""
            SELECT NEW my.iris.model.ai.vo.AiChatAdminVo(
                a.id, c.userId, d.name, a.threadId, b.provider.name,
                b.name, a.prompt, a.completion, a.inputTokenCount, a.outputTokenCount,
                a.totalTokenCount, a.duration, a.createdAt)
            FROM AiChatEntity a
                LEFT JOIN AiModelEntity b ON a.modelId = b.id
                LEFT JOIN AiThreadEntity c ON a.threadId = c.id
                LEFT JOIN UserEntity d ON c.userId = d.id
            WHERE (:threadId IS NULL OR a.threadId = :threadId)
                AND (:username IS NULL OR d.name = :username)
            """)
    Page<AiChatAdminVo> getPage(Pageable pageable, Long threadId, String username);

    List<AiChatEntity> findByThreadIdOrderById(Long threadId);

    @Query("""
            SELECT a
            FROM AiChatEntity a
            WHERE a.threadId = :threadId
            ORDER BY a.id DESC
            LIMIT :limit
            """)
    List<AiChatEntity> findLatest(Long threadId, Long limit);
}
