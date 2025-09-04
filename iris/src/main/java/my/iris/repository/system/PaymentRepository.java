package my.iris.repository.system;

import my.iris.model.system.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    List<PaymentEntity> findAllByOrderByOrderNum();

    @Query(value = "select a from PaymentEntity a where a.enable  order by a.orderNum")
    List<PaymentEntity> findAvailable();

    PaymentEntity findByName(String name);

    @Query(value = "select * from t_payment where name = :name for update", nativeQuery = true)
    PaymentEntity findByNameForWrite(String name);

}
