package my.iris.repository.system;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import my.iris.model.system.entity.SystemEntity;

import java.util.Optional;


@Repository
public interface SystemRepository extends JpaRepository<SystemEntity, Long> {
    Optional<SystemEntity> findFirstByEntityAndAttribute(String entity, String attribute);

    @NativeQuery("SELECT VERSION()")
    String getDbVersion();

    /**
     * update value by entity and attribute
     *
     * @param val       value
     * @param entity    entity
     * @param attribute attribute
     */
    @Modifying
    @Query("""
            UPDATE SystemEntity a
            SET a.value = :val, a.createdAt=CURRENT_TIMESTAMP
            WHERE a.entity = :entity
                AND a.attribute = :attribute
                AND a.value != :val
            """)
    void updateValueByEa(Object val, String entity, String attribute);
}
