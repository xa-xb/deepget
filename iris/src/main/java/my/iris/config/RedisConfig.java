package my.iris.config;

import lombok.Getter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;


@Configuration
@Order(0)
public class RedisConfig {
    @Getter
    private static StringRedisTemplate stringRedisTemplate;

    public RedisConfig(StringRedisTemplate redisTemplate) {
        stringRedisTemplate = redisTemplate;
    }
}
