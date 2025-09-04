package my.iris.repository.user;

import jakarta.persistence.LockModeType;
import my.iris.model.user.entity.UserEntity;
import my.iris.model.user.vo.UserVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Query("""
            SELECT NEW my.iris.model.user.vo.UserVo(
                a.id, a.name, a.mobile, a.email, a.gender,
                b.credits, a.lastSignInAt, a.lastSignInIp, a.createdAt, a.createdIp)
            FROM UserEntity a INNER JOIN UserHotEntity b ON a.id = b.userId
            WHERE (:name IS NULL OR a.name LIKE :name)
                AND ((:mobile) IS NULL OR a.mobile LIKE :mobile)
                AND ((:email) IS NULL OR a.email LIKE :email)
                AND ((:signStatus) IS NULL
                    OR :signStatus = 1
                    OR (:signStatus = 2 AND a.lastSignInAt IS NOT NULL)
                    OR (:signStatus = 3 AND a.lastSignInAt IS NULL))
            """)
    Page<UserVo> getPage(String name, String mobile, String email, Integer signStatus, Pageable pageable);

    @Query("""
            SELECT NEW my.iris.model.user.vo.UserVo(
                a.id, a.name, a.mobile, a.email, a.gender,
                b.credits, a.lastSignInAt, a.lastSignInIp, a.createdAt, a.createdIp)
            FROM UserEntity a INNER JOIN UserHotEntity b ON a.id = b.userId
            WHERE a.id = :id
            """)
    UserVo getUserVoById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select a from UserEntity a where a.email = :email")
    Optional<UserEntity> findByEmailForUpdate(String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select a from UserEntity a where a.id = :userId")
    Optional<UserEntity> findByIdForUpdate(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select a from UserEntity a where a.name = :username")
    Optional<UserEntity> findByNameForUpdate(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select a from UserEntity a where a.mobile = :mobile")
    Optional<UserEntity> findByMobileForUpdate(String mobile);

    @Query("""
            SELECT new my.iris.model.user.vo.UserVo(
                   a.id, a.name, a.mobile, a.email,
                   a.gender, b.credits, a.lastSignInAt, a.lastSignInIp,
                   a.createdAt, a.createdIp)
            FROM UserEntity a LEFT JOIN UserHotEntity b ON a.id = b.userId
            WHERE (:name IS NULL OR a.name LIKE :name)
            """)
    List<UserVo> getList(String name, Pageable pageable);
}
