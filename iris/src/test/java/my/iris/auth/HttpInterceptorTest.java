package my.iris.auth;

import my.iris.model.ApiResult;
import my.iris.util.JsonUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@AutoConfigureMockMvc
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Timeout(2)
class HttpInterceptorTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testUserAuth() throws Exception {
        var response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/ai/chat/prepare")).andReturn().getResponse();
        Assertions.assertEquals(200, response.getStatus());
        var str = response.getContentAsString();
        var apiResult = JsonUtils.parse(str, ApiResult.class);
        Assertions.assertNotNull(apiResult);
        Assertions.assertEquals(2, apiResult.code());
    }
}