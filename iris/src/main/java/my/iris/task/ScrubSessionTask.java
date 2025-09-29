package my.iris.task;

import my.iris.auth.UserToken;
import my.iris.util.Helper;
import my.iris.util.LogUtils;
import my.iris.util.session.Session;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class ScrubSessionTask {
    private final StringRedisTemplate stringRedisTemplate;
    private int all;

    private int deleted;

    public ScrubSessionTask(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }


    /**
     * 检查 redis key, 异常的直接删除，并返回删除的key数量
     */
    private void checkSessionKey() {

        try (Cursor<String> cursor = stringRedisTemplate.scan(
                ScanOptions.scanOptions()
                        .count(1000)
                        .match(Session.REDIS_PREFIX + "*")
                        .build())) {
            while (cursor.hasNext()) {
                all++;
                String key = cursor.next();
                long ttl = Helper.parseNumber(stringRedisTemplate.getExpire(key), Long.class);
                if (key.length() < 20 || ttl == -1L || ttl > Session.TIMEOUT.toSeconds()) {
                    deleted++;
                    stringRedisTemplate.delete(key);
                }
            }
        } catch (Exception e) {
            LogUtils.error(getClass(), e);
        }
    }

    private void checkUserKey() {
        try (Cursor<String> cursor = stringRedisTemplate.scan(
                ScanOptions.scanOptions()
                        .count(1000)
                        .match(UserToken.REDIS_USER_PREFIX + "*")
                        .build())) {
            while (cursor.hasNext()) {
                var key = cursor.next();
                all++;
                var set = stringRedisTemplate.opsForSet().members(key);
                if (set == null) {
                    continue;
                }
                // session id
                set.forEach(sid -> {
                    if (!stringRedisTemplate.hasKey(sid)) {
                        stringRedisTemplate.opsForSet().remove(key, sid);
                        deleted++;
                    }
                });
            }
        } catch (Exception e) {
            LogUtils.error(getClass(), e);
        }
    }

    /**
     * 定时检查session/api session数据
     * 每天04:40 运行一次
     */
    @Scheduled(cron = "0 40 4 * * *")
    public void cleanSession() {
        LogUtils.info(getClass(), "Start scrubbing the session data.");
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        all = deleted = 0;
        /* 检查清理 session */
        checkSessionKey();
        checkUserKey();
        LogUtils.info(getClass(), String.format("Session data scrubbed. Total: %d, Deleted %d", all, deleted));

    }
}
