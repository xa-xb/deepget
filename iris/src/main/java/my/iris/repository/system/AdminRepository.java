package my.iris.repository.system;

import my.iris.model.system.entity.AdminEntity;
import my.iris.model.system.vo.AdminVo;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<AdminEntity, Long> {

    @NativeQuery(value = """
            select a.id,
                   a.user_id,
                   a.enabled,
                   b.name,
                   b.email,
                   b.mobile,
                   a.true_name,
                   a.role_ids,
                   (SELECT string_agg(name, ',')
                    FROM t_admin_role
                    WHERE id = any (SELECT unnest(role_ids)
                                    FROM t_admin
                                    WHERE id = a.id))
                       as role_names,
                   a.last_action_at,
                   a.last_sign_in_at,
                   a.last_sign_in_ip,
                   a.created_at,
                   a.created_ip
            from t_admin a
                     left join t_user b on a.user_id = b.id
            order by last_action_at desc
            """,
            countQuery = """
                    select count(1)
                    from t_admin
                    """)
    Page<AdminVo> getPage(String name, Pageable pageable);

    Optional<AdminEntity> findByUserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select a from AdminEntity a where a.userId = :id")
    AdminEntity findByUserIdForUpdate(long id);

    @Modifying
    @Query(value = "update AdminEntity a set a.lastActionAt = CURRENT_TIMESTAMP where a.userId = :userId")
    void updateLastActionAt(Long userId);

    @Modifying
    @NativeQuery("update t_admin set role_ids = array_remove(role_ids, :roleId)")
    void removeRoleId(Long roleId);
}
