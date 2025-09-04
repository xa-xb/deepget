package my.iris.util;

import my.iris.config.RedisConfig;
import org.springframework.lang.Nullable;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class UUIDUtils {

    static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generates a random UUID.
     *
     * @return A randomly generated UUID.
     */
    public static UUID generateUuidV7() {
        while (true) {
            // 当前毫秒级时间戳
            long timestamp = Instant.now().toEpochMilli();

            // 将 timestamp 拆分为高 48 位
            long msb = (timestamp & 0xFFFFFFFFFFFFL) << 16;

            // 设置版本号：UUIDv7 = version 7（0111）
            msb |= 0x0000000000007000L;

            // 插入高 12 位随机数（位 52~63）
            msb |= RANDOM.nextInt(0x1000);  // 12 bits 随机数

            // 生成 62-bit 的随机数
            long lsb = RANDOM.nextLong() & 0x3FFFFFFFFFFFFFFFL;

            // 设置 variant（前两位是 10）
            lsb |= 0x8000000000000000L;

            var uuid = new UUID(msb, lsb);
            if (Boolean.TRUE.equals(RedisConfig.getStringRedisTemplate().opsForValue()
                    .setIfAbsent("test-uuid:" + uuid, "", Duration.ofMinutes(30)))) {
                // 如果成功设置了键值对，表示 UUID 是唯一的
                return uuid;
            }
        }
    }

    /**
     * Extracts the timestamp from a UUIDv7 string.
     *
     * @param uuid The UUIDv7 to extract the timestamp from.
     * @return The timestamp as an Instant.
     * @throws IllegalArgumentException if the UUID string is invalid or not a version 7 UUID.
     */
    public static Instant getUuidV7Time(UUID uuid) {
        try {
            // Verify it's a version 7 UUID
            if (uuid.version() != 7) {
                throw new IllegalArgumentException("UUID is not version 7");
            }
            // Extract the 48-bit timestamp from MSB
            long msb = uuid.getMostSignificantBits();
            long timestamp = (msb >>> 16) & 0xFFFFFFFFFFFFL; // Reverse the left shift and mask
            return Instant.ofEpochMilli(timestamp);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID string: " + uuid, e);
        }
    }

    @Nullable
    public static UUID getUuid(String uuidStr) {
        if (uuidStr == null || uuidStr.isEmpty()) {
            return null;
        }
        try {
            return UUID.fromString(uuidStr);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}
