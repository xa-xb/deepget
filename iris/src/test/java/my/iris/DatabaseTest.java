package my.iris;


import my.iris.repository.user.UserRepository;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class DatabaseTest {

    @PersistenceContext
    EntityManager em;


    @Resource
    UserRepository userRepository;


    @Test
    @Transactional(rollbackOn = Exception.class)
    public void test() {
    }
}
