package my.iris.repository.ai;

import my.iris.model.ai.entity.AiModelEntity;
import my.iris.model.ai.vo.AiModelVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AiModelRepository extends JpaRepository<AiModelEntity, Long> {

    @Query("""
            SELECT NEW my.iris.model.ai.vo.AiModelVo(
                   a.id, a.name, a.provider.iconSvg, a.sysName, a.openRouterName,
                   a.provider.id, a.provider.name, a.cnyPerToken, a.intro, a.orderNum,
                   a.enabled, a.free, a.createdAt
            )
            FROM AiModelEntity a
            WHERE (:name IS NULL or a.name LIKE :name)
                AND (:providerId IS NULL OR a.provider.id = :providerId)
                AND (:enabled IS NULL OR a.enabled = :enabled)
            ORDER BY a.orderNum, a.name
            """)
    Page<AiModelVo> getPage(Pageable pageable, String name, Long providerId, Boolean enabled);
}
