package my.iris.auth;

import lombok.Getter;

@Getter
public class AdminToken extends Token {
    private final String[] authorize;

    public AdminToken(String[] authorize) {
        this.authorize = authorize == null ? new String[0] : authorize;
    }
}
