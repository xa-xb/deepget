package my.iris.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class UUIDv7Validator implements ConstraintValidator<UUIDv7, String> {

    private static final Pattern UUID_V7_PATTERN = Pattern.compile(
            "^[0-9a-f]{8}-[0-9a-f]{4}-7[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$",
            Pattern.CASE_INSENSITIVE
    );

    @Override
    public void initialize(UUIDv7 constraintAnnotation) {
        // 无需初始化
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // null 值可以用 @NotNull 控制，这里允许 null 通过
        if (value == null) {
            return true;
        }
        return UUID_V7_PATTERN.matcher(value).matches();
    }
}