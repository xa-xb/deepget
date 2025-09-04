package my.iris.auth;

import my.iris.model.user.entity.UserEntity;
import my.iris.util.JsonUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Token {
    private String username;
    private String avatar;
    private String email;
    private long userId;
    private int level;


    public Token() {
    }

    public Token(UserEntity userEntity) {
        username = userEntity.getName();
        avatar = userEntity.getAvatar();
        email = userEntity.getEmail();
        userId = userEntity.getId();
        level = userEntity.getLevel();
    }

    public void copyTo(Token token) {
        token.setUsername(username)
                .setAvatar(avatar)
                .setEmail(email)
                .setUserId(userId)
                .setLevel(level);
    }


    /**
     * validate token
     *
     * @return effective
     */
    @JsonIgnore
    public boolean validate() {
        return userId > 0
                && level >= 0;
    }

    @Override
    public String toString() {
        return JsonUtils.stringify(this);
    }
}
