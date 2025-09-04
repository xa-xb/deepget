package my.iris.model.ai.dto;

import org.junit.jupiter.api.Test;

class AiModelDtoTest {

    @Test
    void test() {
        var dto = new AiModelDto();
        dto.setName(null);
        dto.setSysName("  testModel  ");
        dto.setOpenRouterName("");
    }
}