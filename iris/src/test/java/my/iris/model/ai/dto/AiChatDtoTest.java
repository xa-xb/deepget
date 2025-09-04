package my.iris.model.ai.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AiChatDtoTest {
    @Test
    void test() {
        var obj = new AiChatDto();
        obj.setPrompt("");
        Assertions.assertEquals("", obj.getPrompt());
        Assertions.assertNull(obj.getModelId());
        Assertions.assertNull(obj.getThreadUuid());
    }

}