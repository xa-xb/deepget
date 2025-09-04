package my.iris.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import io.swagger.v3.oas.annotations.media.Schema;
import my.iris.util.Helper;
import my.iris.util.JsonUtils;
import org.springframework.lang.NonNull;

public record ApiResult<T>(
        @Schema(description = "状态码: 1=成功,2=需要登录,3=没有权限,4=错误的请求,5=执行失败",
                example = "1",
                allowableValues = {"1", "2", "3", "4", "5"})
        int code,

        @Schema(description = "返回的消息，配合字段code使用")
        String msg,

        @Schema(description = "返回的数据，code != 1 时返回 null")
        T data
) {
    // Enum for status codes
    public enum Status {
        SUCCESS(1),
        FORBIDDEN(2),
        UNAUTHORIZED(3),
        BAD_REQUEST(4),
        ERROR(5);

        private final int value;

        Status(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }
    }

    public ApiResult {
        if (msg == null) {
            msg = "";
        }
        if (code < 1 || code > 5) {
            throw new IllegalArgumentException("Code must be between 1 and 5");
        }
    }

    @JsonGetter("timestamp")
    @Schema(description = "时间", example = "2025-01-01 08:00:00", format = "date-time")
    public String timestamp() {
        return Helper.now();
    }

    public static <T> ApiResult<T> success() {
        return success("", null);
    }

    public static <T> ApiResult<T> success(T data) {
        return success("", data);
    }

    public static <T> ApiResult<T> success(String msg, T data) {
        return new ApiResult<>(Status.SUCCESS.value(), msg, data);
    }

    public static <T> ApiResult<T> forbidden(String msg) {
        return new ApiResult<>(Status.FORBIDDEN.value(), msg, null);
    }

    public static <T> ApiResult<T> unauthorized(String msg) {
        return new ApiResult<>(Status.UNAUTHORIZED.value(), msg, null);
    }

    public static <T> ApiResult<T> badRequest(String msg) {
        return new ApiResult<>(Status.BAD_REQUEST.value(), msg, null);
    }

    public static <T> ApiResult<T> error(String msg) {
        return new ApiResult<>(Status.ERROR.value(), msg, null);
    }

    @NonNull
    @Override
    public String toString() {
        return JsonUtils.stringify(this);
    }
}
