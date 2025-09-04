package my.iris.service.user;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {

    @Resource
    UserService userService;

    @Test
    void findUserByAccount() {
        userService.findUserByAccount("x");
    }

    @Test
    void findUserByAccountAndPassword() {
        userService.findUserByAccountAndPassword("x", "x");
    }


    @Test
    void getPage() {
        userService.getPage(null, null, null, null, PageRequest.of(0, 10));
    }
}