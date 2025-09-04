package my.iris.util;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SecurityUtilsTest {

    @Resource
    PasswordEncoder passwordEncoder;

    @Test
    void testAes() {
        var randomStr = Helper.randomString(8);
        var encryptBase64 = SecurityUtils.aesEncrypt(randomStr);
        Assertions.assertEquals(randomStr, SecurityUtils.aesDecrypt(encryptBase64));
    }

    @Test
    void testHash() {
        Assertions.assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", SecurityUtils.sha256(""));
        Assertions.assertEquals("a7ffc6f8bf1ed76651c14756a061d662f580ff4de43b49fa82d80a4b80f8434a", SecurityUtils.sha3_256(""));

    }

    @Test
    void testPassword() {
        var pwd = Helper.randomString(8);
        var str = passwordEncoder.encode(pwd);
        Assertions.assertTrue(passwordEncoder.matches(pwd, str));
    }

    @Test
    void testRsa() {
        var str = Helper.randomString(32);
        var encryptStr = SecurityUtils.rsaEncrypt(str);
        Assertions.assertNotNull(encryptStr);
        var decryptStr = SecurityUtils.rsaDecrypt(encryptStr);
        Assertions.assertEquals(str, decryptStr);
    }
}