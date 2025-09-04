package my.iris.auth;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于admin控制接口访问权限：
 * - skipAuth = true：接口不需要登录
 * - skipAuthorization = true：登录后即可访问，无需特定权限
 * - value：指定所需权限，登录后且具备权限才能访问
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Authorize {
    /**
     *
     */
    String[] value() default {};

    // 跳过认证, 不需要认证
    boolean skipAuth() default false;

    // no authorization required
    boolean skipAuthorization() default false;
}
