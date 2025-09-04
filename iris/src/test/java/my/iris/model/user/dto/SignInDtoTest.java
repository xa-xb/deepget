package my.iris.model.user.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SignInDtoTest {

    @Test
    void setAccount() {
        new SignInDto().setAccount(null);
    }
}