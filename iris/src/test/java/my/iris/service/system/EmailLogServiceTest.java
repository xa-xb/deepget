package my.iris.service.system;

import jakarta.annotation.Resource;
import my.iris.service.system.EmailLogService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EmailLogServiceTest {

    @Resource
    EmailLogService emailLogService;

    @Test
    void getLastId() {
        Assertions.assertTrue(emailLogService.getLastId() >= 0);
    }
}