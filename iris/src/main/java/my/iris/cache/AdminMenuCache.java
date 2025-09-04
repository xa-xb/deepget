package my.iris.cache;

import my.iris.model.system.entity.AdminMenuEntity;
import my.iris.repository.system.AdminMenuRepository;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.Getter;
import my.iris.util.JsonUtils;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Getter
public class AdminMenuCache {
    @Getter(AccessLevel.NONE)
    private final AdminMenuRepository adminMenuRepository;
    private List<AdminMenuEntity> rows;

    public AdminMenuCache(AdminMenuRepository adminMenuRepository){
        this.adminMenuRepository = adminMenuRepository;
    }

    @PostConstruct
    public synchronized void update() {
        var list = new LinkedList<AdminMenuEntity>();
        adminMenuRepository.findAllByOrderByOrderNumAscIdAsc().forEach(item->{
            list.add(JsonUtils.clone(item));
        });
        this.rows = list;
        for (var row : rows) {
            if (row.getParentId() == 0L) {
                row.setFullRoute(row.getRoute());
                recursion(row);
            }
        }
    }

    private void recursion(AdminMenuEntity entity) {
        for (var row : rows) {
            if (Objects.equals(row.getParentId(), entity.getId())) {
                row.setFullRoute(entity.getFullRoute() + "/" + row.getRoute());
                recursion(row);
            }
        }
    }

    public String[] getEnabledAuthorizeByIds(List<Long> authIds) {
        Set<String> set = new TreeSet<>();
        for (var authId : authIds) {
            var row = getRowById(authId);
            if (row != null && row.getType() == 3 && row.getEnable()) {
                var parentRow = getRowById(row.getParentId());
                if (parentRow != null && parentRow.getEnable()) {
                    var rootRow = getRowById(parentRow.getParentId());
                    if (rootRow != null && rootRow.getEnable()) {
                        set.add(row.getFullRoute());
                    }
                }
            }
        }
        return set.toArray(String[]::new);
    }

    public AdminMenuEntity getRowById(Long id) {
        for (var row : rows) {
            if (Objects.equals(row.getId(), id)) {
                return row;
            }
        }
        return null;
    }
}
