package my.iris.config;

import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
public class OpenApiConfig {

    private static final String[] IGNORED_PARAMETERS = new String[]{
            "response", "session", "userToken"
    };

    static {
        Arrays.sort(IGNORED_PARAMETERS);
    }

    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> {
            // 设置文档标题和版本号
            openApi.info(new Info().title("api documentation").version("v0.0.1"));

            // 对 tags 进行名称 A-Z 排序
            List<Tag> sortedTags = openApi.getTags().stream()
                    .sorted(Comparator.comparing(Tag::getName)) // 按名称正序 A-Z
                    .toList();
            openApi.setTags(sortedTags);

            // HTTP method 正序排序
            var paths = openApi.getPaths();
            Map<String, PathItem> sortedPaths = paths.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey()) // 按路径名排序，按需改
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (oldValue, newValue) -> oldValue,
                            LinkedHashMap::new));
            openApi.setPaths(new Paths());
            sortedPaths.forEach((name, pathItem) -> {
                // 遍历每个 PathItem，删除不需要展示的参数（如 session, userToken）
                pathItem.readOperations().forEach(op -> {
                    var parameters = op.getParameters();
                    if (parameters != null) {
                        parameters.removeIf(parameter ->
                                Arrays.binarySearch(IGNORED_PARAMETERS, parameter.getName()) >= 0);
                    }
                });
                openApi.getPaths().put(name, pathItem);
            });
        };

    }
}
