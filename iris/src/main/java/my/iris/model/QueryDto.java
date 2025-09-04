package my.iris.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;


/**
 * 通用查询Dto
 */
public record QueryDto(
        @Schema(description = "id", example = "1234")
        Long id,

        @Schema(description = "图片验证码", example = "1234")
        String captcha,

        @Schema(description = "手机/邮箱 验证码", example = "1234")
        String code,

        @Schema(description = "电子邮件", example = "x@example.com")
        @Email
        String email,

        @Schema(description = "IP地址", example = "2001:0db8::6")
        String ip,

        @Schema(description = "手机号码", example = "18812345678")
        String mobile,

        @Schema(description = "名字,用户名", example = "Tom")
        String name,

        @Schema(description = "number 号码,订单号,流水号", example = "1234")
        String no,

        @Schema(description = "操作", example = "sing_in")
        String operation,

        @Schema(description = "当前页", example = "1", minimum = "1", defaultValue = "1")
        @Min(1)
        Integer page,

        @Schema(description = "每页显示的记录数", example = "20", maximum = "200", minimum = "1", defaultValue = "20")
        @Max(200)
        Integer pageSize,

        @Schema(description = "password 密码;使用时需要客户端加密，服务端解密")
        String password,

        @Schema(description = "query 需要查询的内容", example = "keyword")
        String q,

        @Schema(description = "排序字段", example = "id,desc")
        String sort,

        @Schema(description = "用户ID", example = "1234")
        Long userId
) {
    public QueryDto {
        if (captcha != null) captcha = captcha.trim();
        if (code != null) code = code.trim();
        if (email != null) email = email.trim().toLowerCase();
        if (page == null) page = 1;
        if (pageSize == null) pageSize = 20;

    }
}
