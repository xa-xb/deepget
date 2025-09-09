package my.iris.service.email;

import jakarta.annotation.Resource;
import my.iris.model.email.dto.EmailQueryDto;
import my.iris.util.DbUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EmailLogServiceTest {

    @Resource
    EmailLogService emailLogService;

    @Test
    void getLastId() {
        Assertions.assertTrue(emailLogService.getLastId() >= 0);
    }

    @Test
    void getPage() {
        EmailQueryDto queryDto = new EmailQueryDto();
        queryDto.setAction(null);
        queryDto.setFrom(null);
        queryDto.setTo(null);
        emailLogService.getPage(PageRequest.of(
                        0,
                        20,
                        DbUtils.getSort(new String[]{"id"}, null, "id,desc")),
                queryDto);
    }
}