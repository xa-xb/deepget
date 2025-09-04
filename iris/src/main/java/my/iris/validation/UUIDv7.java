package my.iris.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UUIDv7Validator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface UUIDv7 {
    String message() default "must be a valid UUIDv7";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
