package my.iris.service.user;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserLogServiceTest {

    @Resource
    UserLogService userLogService;

    @Test
    void getPage() {
        userLogService.getPage(null, null, null, PageRequest.of(0, 10));
    }
}