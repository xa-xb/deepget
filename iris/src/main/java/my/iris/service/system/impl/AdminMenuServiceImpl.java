package my.iris.service.system.impl;

import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import my.iris.cache.AdminMenuCache;
import my.iris.model.ApiResult;
import my.iris.model.system.entity.AdminMenuEntity;
import my.iris.repository.system.AdminMenuRepository;
import my.iris.service.system.AdminLogService;
import my.iris.service.system.AdminMenuService;
import my.iris.util.DbUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class AdminMenuServiceImpl implements AdminMenuService {
    @Resource
    AdminLogService adminLogService;


    @Resource
    AdminMenuCache adminMenuCache;

    @Resource
    AdminMenuRepository adminMenuRepository;


    /**
     * delete entity
     *
     * @param id id
     * @return result
     */
    @Override
    public ApiResult<Void> delete(Long id) {
        var entity = DbUtils.findByIdForUpdate(AdminMenuEntity.class, id);
        if (entity == null) {
            return null;
        }
        if (adminMenuRepository.countByParentId(id) > 0) {
            return ApiResult.error("请先删除子项");
        }
        adminMenuRepository.delete(entity);
        adminMenuRepository.flush();
        adminMenuCache.update();
        adminLogService.addLog("删除菜单", entity);
        return ApiResult.success();
    }

    @Override
    public ApiResult<List<AdminMenuEntity>> getList(AdminMenuEntity condition) {
        if (condition == null) {
            condition = new AdminMenuEntity();
        }
        return ApiResult.success(adminMenuRepository.findAll(Example.of(condition), Sort.by("orderNum", "id")));
    }

    public List<Menu> getMenu(String[] authorize, boolean skipAuthorize) {
        List<Menu> menus = new ArrayList<>();
        // home page
        var menu = new Menu(null, new MenuMeta(null, null, "House", null, false), "/");
        menu.addChild(new Menu("main/home", new MenuMeta(true, "首页", "House", true, false), "home"));
        menus.add(menu);

        var rows = adminMenuCache.getRows();
        // directory
        rows.stream().filter(row -> row.getParentId() == 0L).forEach(item -> {
            var menuItem = new Menu(item.getPage(),
                    new MenuMeta(item.getCache(), item.getName(), item.getIcon(), null, item.getVisible()), item.getRoute());
            // menu
            rows.stream().filter(row -> Objects.equals(row.getParentId(), item.getId())).forEach(item1 -> {

                // check authorize
                if (skipAuthorize || ObjectUtils.containsElement(authorize, item1.getFullRoute() + "/query")) {
                    menuItem.addChild(new Menu(item1.getPage(),
                            new MenuMeta(item1.getCache(), item1.getName(), item1.getIcon(), null, item1.getVisible()), item1.getRoute()));
                }
            });
            if (menuItem.children != null) {
                menus.add(menuItem);
            }
        });

        return menus;
    }


    @Override
    public ApiResult<Void> save(AdminMenuEntity adminMenuEntity) {
        switch (adminMenuEntity.getType()) {
            case 1 -> {
                if (adminMenuEntity.getParentId() != 0) {
                    return ApiResult.error("目录的上级需为空");
                }
                if (!adminMenuEntity.getRoute().startsWith("/")) {
                    return ApiResult.error("目录路由地址必须以 / 开头");
                }
                adminMenuEntity.setPage("");
            }
            case 2 -> {
                if (adminMenuEntity.getParentId() == 0L) {
                    return ApiResult.error("菜单的上级不能为空");
                }
                if (Strings.isBlank(adminMenuEntity.getPage())) {
                    return ApiResult.error("组件路径不能为空");
                }
            }
            case 3 -> {
                if (adminMenuEntity.getParentId() == 0L) {
                    return ApiResult.error("按钮的上级不能为空");
                }
                adminMenuEntity.setIcon("")
                        .setPage("")
                        .setVisible(false);
            }
        }

        if (adminMenuEntity.getParentId() != 0L) {
            if (Objects.equals(adminMenuEntity.getId(), adminMenuEntity.getParentId())) {
                return ApiResult.error("上级不能选择自己");
            }
            var parentEntity = DbUtils.findByIdForUpdate(AdminMenuEntity.class, adminMenuEntity.getParentId());
            if (parentEntity == null) {
                return ApiResult.error("上级不存在");
            }
            if (adminMenuEntity.getType() == 2 && parentEntity.getType() != 1) {
                return ApiResult.error("菜单的上级类型必须为目录");
            }
            if (adminMenuEntity.getType() == 3 && parentEntity.getType() != 2) {
                return ApiResult.error("按钮的上级类型必须为菜单");
            }
        }
        adminMenuRepository.save(adminMenuEntity);
        adminMenuCache.update();
        adminLogService.addLog(adminMenuEntity.getId() == null ? "新建菜单" : "编辑菜单", adminMenuEntity);
        return ApiResult.success();
    }
}
