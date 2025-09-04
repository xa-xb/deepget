package my.iris.util.session;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Map;

/**
 * session interface
 */
public interface BaseSession {
    // 过期时间
    Duration TIMEOUT = Duration.ofDays(30);

    /**
     * 生成 session id
     *
     * @return session id
     */
    static String generalId() {
        return new BigInteger(SecureRandom.getSeed(64)).abs().toString(36);
    }

    /**
     * 销毁会话信息
     */
    void clear();

    /**
     * get value by ke
     *
     * @param key session key
     * @return session value
     */
    String get(String key);

    /**
     * 返回所有键值对
     *
     * @return map
     */
    Map<String, String> getAll();

    /**
     * 获取session id, 并发送到客户端
     *
     * @param canCreate 不存在则新建
     * @return session id
     */
    String getId(boolean canCreate);

    /**
     * delete key
     *
     * @param keys keys
     */
    void remove(String... keys);

    /**
     * set key value
     *
     * @param key   session key
     * @param value session value
     */
    void set(String key, String value);

    void set(Map<String, String> keyValues);

}
