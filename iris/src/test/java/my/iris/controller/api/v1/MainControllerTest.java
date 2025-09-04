package my.iris.controller.api.v1;

import jakarta.servlet.http.Cookie;
import my.iris.model.ApiResult;
import my.iris.model.user.dto.NewUserDto;
import my.iris.model.user.entity.UserEntity;
import my.iris.model.user.entity.UserLogEntity;
import my.iris.repository.user.UserLogRepository;
import my.iris.repository.user.UserRepository;
import my.iris.service.user.UserService;
import my.iris.util.CaptchaUtils;
import my.iris.util.Helper;
import my.iris.util.JsonUtils;
import my.iris.util.SecurityUtils;
import my.iris.util.session.Session;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Objects;

@AutoConfigureMockMvc
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Timeout(2)
class MainControllerTest {
    String captcha;
    String sessionId;
    String username;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserLogRepository userLogRepository;


    @Order(1)
    @Test
    void captcha() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/main/captcha"))
                .andReturn().getResponse();
        sessionId = Objects.requireNonNull(response.getCookie(Session.COOKIE_NAME)).getValue();
        Session session = new Session(sessionId);
        captcha = session.get(CaptchaUtils.CAPTCHA_SESSION_NAME);
        Assertions.assertEquals(CaptchaUtils.SIZE, captcha.length());
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertTrue(response.getContentAsByteArray().length > 200);
        var contentType = response.getContentType();
        Assertions.assertNotNull(contentType);
        Assertions.assertTrue(contentType.startsWith("image/png"));
    }

    @Order(2)
    @Test
    void signUp() throws Exception {
        username = null;
        while (username == null) {
            var tmp = "test" + Helper.randomString(6);
            if (userService.findUserByAccount(tmp) == null) username = tmp;
        }
        var newUserDto = new NewUserDto(username, SecurityUtils.rsaEncrypt(username), captcha);
        MockHttpServletResponse response = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/v1/main/sign_up")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(JsonUtils.stringify(newUserDto))
                                .cookie(new Cookie(Session.COOKIE_NAME, sessionId))
                )
                .andReturn().getResponse();
        var apiResult = JsonUtils.parse(response.getContentAsString(), ApiResult.class);
        Assertions.assertNotNull(apiResult);
        Assertions.assertEquals(1L, apiResult.code());
    }

    @Order(3)
    @Test
    void signIn() {
    }

    @Test
    public void testPage() throws Exception {
        Assertions.assertEquals(405, mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/main/sign_in")).andReturn().getResponse().getStatus());
        Assertions.assertEquals(200, mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/main/sign_in")).andReturn().getResponse().getStatus());
    }

    @AfterAll
    void cleanupAfterAll() {
        var example = Example.of(new UserEntity().setName(username));
        userRepository.findOne(example).ifPresent(user -> {
            userLogRepository.deleteById(user.getId());
            userRepository.deleteById(user.getId());
            userLogRepository.findAll(Example.of(new UserLogEntity().setUserId(user.getId()))).
                    forEach(userLog -> userLogRepository.deleteById(userLog.getId()));
        });
    }
}