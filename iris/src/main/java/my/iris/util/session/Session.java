package my.iris.util.session;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import my.iris.config.RedisConfig;
import my.iris.util.Helper;
import my.iris.util.TaskContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.util.HashMap;
import java.util.Map;

/**
 * session 实现，用于替换系统session机制
 * 相关配置等在 HttpInterceptor SessionArgumentResolvers 中实现
 */
public class Session implements BaseSession {
    // cooke name
    public static final String COOKIE_NAME = "sid";

    //redis prefix
    public static final String REDIS_PREFIX = "sid:";

    private final HttpServletResponse response;
    // session id
    private String id;

    public static ResponseCookie newCookie(String id) {
        return ResponseCookie.from(COOKIE_NAME, id)
                .httpOnly(true)
                .maxAge(TIMEOUT)
                .path("/")
                .sameSite("Lax")
                .build();
    }

    public Session() {
        response = null;
        id = getId(true);
        flush();
    }

    /**
     * create session object by session id
     *
     * @param sessionId exist session id
     */
    public Session(String sessionId) {
        response = null;
        id = sessionId;
        if (!flush()) {
            throw new IllegalArgumentException("session not exists");
        }
    }

    /**
     * create session object
     *
     * @param request  request
     * @param response response
     */
    public Session(HttpServletRequest request, HttpServletResponse response) {
        this.response = response;
        var cookies = request.getCookies();
        if (cookies != null) {
            for (var cookie : cookies) {
                if (cookie.getName().equals(COOKIE_NAME)) {
                    id = cookie.getValue();
                    break;
                }
            }
            if (id != null && !flush()) {
                id = null;
            }
        }
        TaskContext.setSession(this);
    }

    /**
     * delete
     */
    @Override
    public void clear() {
        if (id != null) {
            RedisConfig.getStringRedisTemplate().delete(REDIS_PREFIX + id);
        }

    }


    /**
     * 刷新过期时间
     */
    private boolean flush() {
        if (id != null) {
            Boolean b = RedisConfig.getStringRedisTemplate().expire(REDIS_PREFIX + id, TIMEOUT);
            if (b != null) {
                return b;
            }
        }
        return false;
    }


    @Override
    public String get(String key) {
        return (String) RedisConfig.getStringRedisTemplate().opsForHash().get(REDIS_PREFIX + id, key);
    }


    @Override
    public Map<String, String> getAll() {
        Map<String, String> map = new HashMap<>();
        if (id != null) {
            RedisConfig.getStringRedisTemplate().opsForHash().entries(REDIS_PREFIX + id).
                    forEach((key, val) -> map.put((String) key, (String) val));
        }
        return map;
    }


    /**
     * 获取session id, 并发送到客户端
     *
     * @param canCreate 不存在则新建
     * @return session id
     */
    @Override
    public String getId(boolean canCreate) {
        if (id == null && canCreate) {
            do {
                id = BaseSession.generalId();
            }
            while (!RedisConfig.getStringRedisTemplate().opsForHash()
                    .putIfAbsent(REDIS_PREFIX + id, "created_at", Helper.now()));
            flush();
            if (response != null) {
                response.addHeader(HttpHeaders.SET_COOKIE, newCookie(id).toString());
            }
        }
        return id;
    }

    @Override
    public void remove(String... keys) {
        if (id != null) {
            RedisConfig.getStringRedisTemplate().opsForHash().delete(REDIS_PREFIX + id, (Object[]) keys);
        }
    }

    /**
     * 设置键值对
     *
     * @param key   string
     * @param value object
     */
    @Override
    public void set(String key, String value) {
        if (value != null) {
            RedisConfig.getStringRedisTemplate().opsForHash()
                    .put(REDIS_PREFIX + getId(true), key, value);
        }

    }

    @Override
    public void set(Map<String, String> keyValues) {
        keyValues.entrySet().removeIf(entry -> entry.getValue() == null);
        RedisConfig.getStringRedisTemplate().opsForHash()
                .putAll(REDIS_PREFIX + getId(true), keyValues);
    }
}
