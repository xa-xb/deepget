package my.iris.service.system;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AdminServiceTest {

    @Resource
    AdminService adminService;

    @Test
    void getPage() {
        adminService.getPage(null, PageRequest.of(0, 10));
    }
}