package my.iris.model;

import java.util.List;

public record ListVo<T>(
        List<T> rows
) implements ListableVo<T> {
}
