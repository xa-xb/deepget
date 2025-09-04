package my.iris.service.ai;

import my.iris.auth.UserToken;
import my.iris.util.Helper;
import my.iris.util.JsonUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TokenTests {

    @Test
    public void test() {
        var str = Helper.randomString(4);
        UserToken userToken = new UserToken();
        userToken.setAvatar(str);
        var token = JsonUtils.parse(userToken.toString(), UserToken.class);
        assert token != null;
        Assertions.assertEquals(token.getAvatar(), str);
    }
}
