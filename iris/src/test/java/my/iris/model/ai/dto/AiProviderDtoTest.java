package my.iris.model.ai.dto;

import org.junit.jupiter.api.Test;

class AiProviderDtoTest {

    @Test
    void test() {
        var obj = new AiProviderDto();
        obj.setIconSvg("<svg>...</svg>");
        obj.setApiKey(" 123");
        obj.setUrl("https://api.example.com/ ");
        obj.setApiUrl("https://api.example.com/ ");
    }
}