package my.iris.model.user.vo;

public record TokenLogVo(
        Long id,
        String createdAt,
        String name,
        Long amount,
        Long balance,
        String note
) {
}
