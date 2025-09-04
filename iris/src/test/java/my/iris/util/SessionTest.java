package my.iris.util;

import my.iris.util.session.Session;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SessionTest {
    private Session session;

    @BeforeAll
    void setUp() {
        session = new Session();
    }

    @Test
    void testSetAndGet() {
        // 测试基本的 set 和 get 操作
        session.set("key1", "value1");
        assertEquals("value1", session.get("key1"));
    }

    @Test
    void testRemove() {
        // 测试移除键值对
        session.set("key1", "value1");
        assertNotNull(session.get("key1"));

        session.remove("key1");
        assertNull(session.get("key1"));
    }

    @Test
    void testClear() {
        // 测试清空所有数据
        session.set("key1", "value1");
        session.set("key2", "value2");

        session.clear();

        assertNull(session.get("key1"));
        assertNull(session.get("key2"));
    }

    @Test
    void testNullValues() {
        // 测试空值处理
        session.set("nullKey", null);
        assertNull(session.get("nullKey"));
        Map<String, String> map = new HashMap<>();
        map.put("nullkey1", null);
        map.put("nullkey2", "");
        session.set(map);
        assertNull(session.get("nullKey2"));
    }

    @Test
    void testNonExistentKey() {
        // 测试获取不存在的键
        assertNull(session.get("nonexistent"));
    }

    @AfterAll
    void tearDown() {
        if (session != null) {
            session.clear();
        }
    }
}