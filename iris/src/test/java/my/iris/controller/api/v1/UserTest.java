package my.iris.controller.api.v1;

import my.iris.model.user.entity.UserEntity;
import my.iris.repository.user.UserRepository;
import my.iris.util.CaptchaUtils;
import my.iris.util.Helper;
import my.iris.util.UserAgent;
import my.iris.util.session.Session;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.Timeout;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.StringUtils;

import java.util.Objects;

@AutoConfigureMockMvc
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserTest {
    @Resource
    UserRepository userRepository;

    @Resource
    private MockMvc mockMvc;

    @Test
    public void testPage() {
    }

    // @Test
    @Timeout(5)
    @Transactional
    public void signUpTest() throws Exception {
        var response = mockMvc.perform(MockMvcRequestBuilders.get("/captcha")).andReturn().getResponse();
        String sid = Objects.requireNonNull(response.getCookie(Session.COOKIE_NAME)).getValue();
        Session session = new Session(sid);
        String code = session.get(StringUtils.uncapitalize(CaptchaUtils.class.getSimpleName()));
        String email;
        String password = Helper.randomString(8) + "1!";
        do {
            email = "test" + Helper.randomString(8);
        } while (userRepository.findOne(Example.of(new UserEntity().setEmail(email))).isEmpty());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/user/register").cookie(response.getCookies()).param("name", email).param("password", password).param("password1", password).param("captcha", code);
        MvcResult registerResult = mockMvc.perform(requestBuilder).andReturn();
        Assertions.assertEquals(200, registerResult.getResponse().

                getStatus());
        UserEntity userEntity = userRepository.findByEmailForUpdate(email).orElse(null);
        Assertions.assertNotNull(userEntity);
        userRepository.saveAndFlush(userEntity);

        String[] urls = {"/user/address", "/user/central", "/user/info", "/user/order", "/user/password"};
        for (
                var url : urls) {
            Assertions.assertNotEquals(200, mockMvc.perform(MockMvcRequestBuilders.get(url)).andReturn().getResponse().getStatus());
            Assertions.assertNotEquals(200, mockMvc.perform(MockMvcRequestBuilders.get(url).header("user-agent", UserAgent.IPHONE_SAFARI)).andReturn().getResponse().getStatus());
            Assertions.assertEquals(200, mockMvc.perform(MockMvcRequestBuilders.get(url).cookie(response.getCookies())).andReturn().getResponse().getStatus());
            Assertions.assertEquals(200, mockMvc.perform(MockMvcRequestBuilders.get(url).cookie(response.getCookies()).header("user-agent", UserAgent.IPHONE_SAFARI)).andReturn().getResponse().getStatus());
        }
        userRepository.deleteById(userEntity.getId());
        userRepository.flush();
    }

}
