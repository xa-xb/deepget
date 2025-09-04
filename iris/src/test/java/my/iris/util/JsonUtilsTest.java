package my.iris.util;

import my.iris.util.Helper;
import my.iris.util.JsonUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;
import java.util.random.RandomGenerator;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JsonUtilsTest {
    @Test
    public void test() {
        var obj = newTestObj();
        obj.name = Helper.randomString(6);
        obj.num = RandomGenerator.getDefault().nextLong();
        obj.map = Map.of(Helper.randomString(4), Helper.randomString(4));
        var json = JsonUtils.stringify(obj);
        var obj1 = JsonUtils.parse(json, TestObject.class);
        assert obj1 != null;
        Assertions.assertEquals(obj.getName(), obj1.name);
        Assertions.assertEquals(obj.getNum(), obj1.num);
        Assertions.assertEquals(obj, obj1);

    }


    TestObject newTestObj() {
        var obj = new TestObject();
        obj.name = Helper.randomString(6);
        obj.num = RandomGenerator.getDefault().nextLong();
        obj.map = Map.of(Helper.randomString(4), Helper.randomString(4));
        return obj;
    }

    @Test
    void parseList() {
        var list = JsonUtils.parseList("[1,2,3]", Integer.class);
        Assertions.assertEquals(3, list.size());
    }

    @Test
    void testClone() {
        var obj = newTestObj();
        var obj1 = JsonUtils.clone(obj);
        Assertions.assertEquals(obj, obj1);
    }


    public static class TestObject {
        String name;
        Long num;
        Map<String, String> map;

        public String getName() {
            return name;
        }

        public Long getNum() {
            return num;
        }

        public Map<String, String> getMap() {
            return map;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof TestObject o) {
                return o.name.equals(name)
                        && o.num.equals(num)
                        && getMap().equals(o.map);
            }
            return false;
        }
    }
}
