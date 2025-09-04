package my.iris.repository.system;

import my.iris.model.system.entity.AdminRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AdminRoleRepository extends JpaRepository<AdminRoleEntity, Long> {

    /**
     * get available authorize in str
     *
     * @param roleIds role ids
     * @return available authorize str
     */
    @Query("""
            SELECT a.id
            FROM AdminRoleEntity a
            WHERE a.id IN :roleIds
            ORDER BY a.orderNum, a.id
            """)
    List<Long> getAvailableRolesId(List<Long> roleIds);

    List<AdminRoleEntity> findAllByOrderByOrderNum();

    List<AdminRoleEntity> findAllByIdIn(List<Long> ids);

    Optional<AdminRoleEntity> findByName(String name);
}
