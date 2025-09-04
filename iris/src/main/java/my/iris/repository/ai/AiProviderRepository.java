package my.iris.repository.ai;

import my.iris.model.ai.entity.AiProviderEntity;
import my.iris.model.ai.vo.AiProviderVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AiProviderRepository extends JpaRepository<AiProviderEntity, Long> {

    @Query("""
            SELECT NEW my.iris.model.ai.vo.AiProviderVo(
                a.id, a.name, a.iconSvg, a.apiCompatible, a.url,
                a.openRouter, a.apiUrl, a.apiKey, a.orderNum, a.createdAt)
            FROM AiProviderEntity a
            ORDER BY a.orderNum, a.name
            """)
    List<AiProviderVo> getList();
}
