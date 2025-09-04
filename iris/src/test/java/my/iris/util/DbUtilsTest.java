package my.iris.util;

import my.iris.model.user.entity.UserEntity;
import my.iris.util.DbUtils;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DbUtilsTest {

    @BeforeAll
    void setup() {
    }

    @Test
    void camelToSnake() {
        Assertions.assertEquals("user_id", DbUtils.camelToSnake("userId"));
    }

    @Test
    void getTableNameByEntity() {
        Assertions.assertEquals("t_user", DbUtils.getTableName(UserEntity.class));
    }

    @Test
    void snakeToCamel() {
        Assertions.assertEquals("userId", DbUtils.snakeToCamel("user_id"));
    }

    @AfterAll
    void tearDown() {

    }
}