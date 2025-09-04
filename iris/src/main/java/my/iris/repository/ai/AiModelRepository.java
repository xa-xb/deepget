package my.iris.repository.ai;

import my.iris.model.ai.entity.AiModelEntity;
import my.iris.model.ai.vo.AiModelClientVo;
import my.iris.model.ai.vo.AiModelVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AiModelRepository extends JpaRepository<AiModelEntity, Long> {

    @Query("""
            SELECT NEW my.iris.model.ai.vo.AiModelVo(
                   a.id, a.name, a.provider.iconSvg, a.sysName, a.openRouterName,
                   a.provider.id, a.provider.name, a.cnyPerToken, a.intro, a.orderNum,
                   a.enabled, a.free, a.createdAt
            )
            FROM AiModelEntity a
            ORDER BY a.orderNum, a.name
            """)
    List<AiModelVo> getList();
}
