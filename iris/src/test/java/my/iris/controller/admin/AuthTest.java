package my.iris.controller.admin;

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
class AuthTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void authTest() throws Exception {
        var resp = mockMvc.perform(MockMvcRequestBuilders.post("/admin/system/role/add")).andReturn().getResponse();
        Assertions.assertEquals(200, resp.getStatus());
        var apiResult = JsonUtils.parse(resp.getContentAsString(), ApiResult.class);
        Assertions.assertNotNull(apiResult);
        Assertions.assertEquals(2, apiResult.code());
    }

    @Test
    void profileTest() throws Exception {
        var resp = mockMvc.perform(MockMvcRequestBuilders.post("/admin/main/profile")).andReturn().getResponse();
        Assertions.assertEquals(200, resp.getStatus());
        var apiResult = JsonUtils.parse(resp.getContentAsString(), ApiResult.class);
        Assertions.assertNotNull(apiResult);
        Assertions.assertEquals(1, apiResult.code());
    }

}