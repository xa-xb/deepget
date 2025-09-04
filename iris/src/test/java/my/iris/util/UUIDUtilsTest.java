package my.iris.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UUIDUtilsTest {

    @Test
    void test() {
        for (int i = 0; i < 10; i++) {
            var uuid = UUIDUtils.generateUuidV7();
            Assertions.assertNotNull(UUIDUtils.getUuidV7Time(uuid));
        }
    }
}