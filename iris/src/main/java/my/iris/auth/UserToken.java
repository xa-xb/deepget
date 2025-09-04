package my.iris.auth;

import my.iris.config.RedisConfig;
import my.iris.model.user.entity.UserEntity;
import my.iris.util.JsonUtils;
import my.iris.util.session.Session;

import java.util.Set;

public class UserToken extends Token {
    public static final String REDIS_USER_PREFIX = "user:";
    public static final String TOKEN_HASH_KEY = "userToken";

    public UserToken() {
    }

    public UserToken(UserEntity userEntity) {
        super(userEntity);
    }

    public static void deleteToken(long uid, String redisKey) {
        RedisConfig.getStringRedisTemplate().delete(redisKey);
        RedisConfig.getStringRedisTemplate().opsForSet().remove(REDIS_USER_PREFIX + uid, redisKey);
    }

    /**
     * @param session user session
     * @return user token
     */
    public static UserToken from(Session session) {
        if (session == null || session.getId(false) == null) {
            return null;
        }
        String redisKey = Session.REDIS_PREFIX + session.getId(false);
        String userTokenStr = session.get(TOKEN_HASH_KEY);
        if (userTokenStr != null) {
            UserToken userToken = UserToken.from(userTokenStr);
            if (userToken == null) {
                return null;
            }
            Boolean exists = RedisConfig.getStringRedisTemplate().opsForSet().isMember(REDIS_USER_PREFIX + userToken.getUserId(), redisKey);
            if (exists == null || !exists) {
                session.clear();
                return null;
            }
            return userToken;
        }
        return null;
    }

    /**
     * get toke by token, for api
     *
     * @param json String
     * @return user token
     */
    public static UserToken from(String json) {
        var userToken = JsonUtils.parse(json, UserToken.class);
        if (userToken != null && userToken.validate()) {
            return userToken;
        }
        return null;
    }


    /**
     * save token, for http request
     *
     * @param session session
     */
    public UserToken save(Session session) {
        String redisKey = Session.REDIS_PREFIX + session.getId(true);
        session.set(TOKEN_HASH_KEY, toString());
        RedisConfig.getStringRedisTemplate().opsForSet().add(REDIS_USER_PREFIX + getUserId(), redisKey);
        updateRedis(redisKey);
        return this;
    }


    /**
     * Update current user's token in all active Redis sessions (except excluded key)
     *
     * @param excludeRedisKey exclude redis key(session)
     */
    private void updateRedis(String excludeRedisKey) {
        Set<String> set = RedisConfig.getStringRedisTemplate().opsForSet().members(REDIS_USER_PREFIX + getUserId());
        if (set == null || set.isEmpty()) return;
        set.forEach(redisKey -> {
            if (redisKey.equals(excludeRedisKey)) {
                return;
            }
            UserToken userToken = null;
            Object obj = RedisConfig.getStringRedisTemplate().opsForHash().get(redisKey, TOKEN_HASH_KEY);
            if (obj instanceof String str) {
                userToken = UserToken.from(str);
            }
            if (userToken == null || userToken.getUserId() != getUserId()) {
                deleteToken(getUserId(), redisKey);
                return;
            }
            RedisConfig.getStringRedisTemplate().opsForHash().put(redisKey, TOKEN_HASH_KEY, toString());
        });
    }
}
