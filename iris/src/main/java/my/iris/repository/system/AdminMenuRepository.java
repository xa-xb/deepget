package my.iris.repository.system;

import my.iris.model.system.entity.AdminMenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminMenuRepository extends JpaRepository<AdminMenuEntity, Long> {

    /**
     * get available authorize in str
     *
     * @param authIds auth ids
     * @return available authorize str
     */
    @Query("""
            SELECT a.id
            FROM AdminMenuEntity a
            WHERE a.type = 3
                AND a.id IN :authIds
            """)
    List<Long> getAvailableAuthorize(List<Long> authIds);

    long countByParentId(Long parent);

    List<AdminMenuEntity> findAllByOrderByOrderNumAscIdAsc();
}
