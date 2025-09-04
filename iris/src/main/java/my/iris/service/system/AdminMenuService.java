package my.iris.service.system;

import my.iris.model.ApiResult;
import my.iris.model.system.entity.AdminMenuEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.domain.Example;

import java.util.ArrayList;
import java.util.List;

public interface AdminMenuService {
    ApiResult<Void> delete(Long id);
    ApiResult<List<AdminMenuEntity>> getList(AdminMenuEntity condition);
    List<Menu> getMenu(String[] authorize, boolean skipAuthorize);
    ApiResult<Void> save(AdminMenuEntity adminMenuEntity);

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    class Menu {
        public List<Menu> children;
        public String page;
        public MenuMeta meta;
        public String path;
        public String redirect;

        public Menu(String page, MenuMeta meta, String path) {
            this.page = page;
            this.meta = meta;
            this.path = path;
        }

        public void addChild(Menu child) {
            if (children == null) {
                children = new ArrayList<>();
            }
            children.add(child);
        }
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    record MenuMeta(
            Boolean cache, String title,
            String icon, Boolean hideClose,
            Boolean visible
    ) {
    }
}