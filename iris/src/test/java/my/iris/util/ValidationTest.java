package my.iris.util;

import jakarta.annotation.Resource;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ValidationTest {
    @Resource
    private Validator validator;

    @Test
    public void test() {
        TestObj obj = new TestObj();
        var violations = validator.validate(obj);
        violations = validator.validate(obj);
        Assertions.assertEquals(1, violations.size());
        obj.name = "a";
        violations = validator.validate(obj);
        Assertions.assertEquals(0, violations.size());
    }

    static class TestObj {
        @NotNull
        private String name;
    }
}
