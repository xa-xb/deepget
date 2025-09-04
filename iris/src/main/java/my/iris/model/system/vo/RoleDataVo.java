package my.iris.model.system.vo;

import java.util.List;

public record RoleDataVo(
        Long id,
        String name,
        List<RoleDataVo> children
) {
}
