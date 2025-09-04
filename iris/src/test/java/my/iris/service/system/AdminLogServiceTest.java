package my.iris.service.system;

import jakarta.annotation.Resource;
import my.iris.service.system.AdminLogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AdminLogServiceTest {

    @Resource
    AdminLogService adminLogService;

    @Test
    void getPage() {
        adminLogService.getPage(null,null,null, PageRequest.of(0, 10));
    }
}