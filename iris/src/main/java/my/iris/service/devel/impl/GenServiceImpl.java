package my.iris.service.devel.impl;

import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import my.iris.cache.AdminMenuCache;
import my.iris.cache.SystemCache;
import my.iris.config.AppConfig;
import my.iris.model.ApiResult;
import my.iris.model.devel.dto.GenDto;
import my.iris.model.devel.vo.GenVo;
import my.iris.model.system.entity.AdminMenuEntity;
import my.iris.repository.system.AdminMenuRepository;
import my.iris.service.devel.GenService;
import my.iris.service.system.AdminLogService;
import my.iris.util.DbUtils;
import my.iris.util.LogUtils;
import org.springframework.data.domain.Example;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

@Service
@Transactional
public class GenServiceImpl implements GenService {

    @Resource
    JdbcClient jdbcClient;

    @Resource
    private AdminMenuRepository adminMenuRepository;

    @Resource
    private AdminMenuCache adminMenuCache;

    @Resource
    private AdminLogService adminLogService;

    @Resource
    private SystemCache systemCache;

    @Override
    public ApiResult<GenVo> list() {
        return ApiResult.success(new GenVo(systemCache.getFrontendPath()));
    }

    @Override
    public ApiResult<String> add(GenDto genDto) {
        Config config;
        try {
            config = Config.of(genDto);
        } catch (Exception e) {
            return ApiResult.error(e.getMessage());
        }

        var result = new StringBuilder(config.toString());

        createTable(config, result);
        createMenu(config, result);

        String templateStr = readFile(Path.of(config.templateDir, "entity.java.txt"));
        writeFile(
                Path.of(config.codeDir, "model", config.moduleName, "entity", config.businessName + "Entity.java"),
                formatCode(templateStr, config), result);

        templateStr = readFile(Path.of(config.templateDir, "dto.java.txt"));
        writeFile(
                Path.of(config.codeDir, "model", config.moduleName, "dto", config.businessName + "Dto.java"),
                formatCode(templateStr, config), result);

        templateStr = readFile(Path.of(config.templateDir, "vo.java.txt"));
        writeFile(
                Path.of(config.codeDir, "model", config.moduleName, "vo", config.businessName + "Vo.java"),
                formatCode(templateStr, config), result);

        templateStr = readFile(Path.of(config.templateDir, "repository.java.txt"));
        writeFile(
                Path.of(config.codeDir, "repository", config.moduleName, config.businessName + "Repository.java"),
                formatCode(templateStr, config), result);

        templateStr = readFile(Path.of(config.templateDir, "service.java.txt"));
        writeFile(
                Path.of(config.codeDir, "service", config.moduleName, config.businessName + "Service.java"),
                formatCode(templateStr, config), result);

        templateStr = readFile(Path.of(config.templateDir, "serviceImpl.java.txt"));
        writeFile(
                Path.of(config.codeDir, "service", config.moduleName, "impl", config.businessName + "ServiceImpl.java"),
                formatCode(templateStr, config), result);

        templateStr = readFile(Path.of(config.templateDir, "controller.java.txt"));
        writeFile(
                Path.of(config.codeDir, "controller", "admin", config.moduleName, config.businessName + "Controller.java"),
                formatCode(templateStr, config), result);

        createApiFile(config, result);
        templateStr = readFile(Path.of(config.templateDir, "page.vue.txt"));
        writeFile(
                Path.of(config.frontendPath, "src", "views", config.moduleName, config.businessName + "Page.vue"),
                formatCode(templateStr, config), result);
        adminLogService.addLog("生成代码", config);
        return ApiResult.success(result.toString());
    }


    String formatCode(String template, Config config) {
        return template
                .replace("${moduleName}", config.moduleName)
                .replace("${businessName}", config.businessName)
                .replace("${businessNameVar}", config.businessNameVar)
                .replace("${menuName}", config.menuName)
                .replace("${packagePrefix}", config.packagePrefix)
                .replace("${tableName}", config.tableName);

    }

    String readFile(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException exception) {
            LogUtils.error(getClass(), exception);
            return null;
        }
    }

    void writeFile(Path path, String content, StringBuilder log) {
        if (path.toFile().exists()) {
            log.append("file already exists: ").append(path).append("\n");
            return;
        }
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, content);
            log.append("file written: ").append(path).append("\n");
        } catch (IOException exception) {
            LogUtils.error(getClass(), exception);
            log.append("file write error: ").append(path).append("\n");
        }
    }

    @SuppressWarnings("all")
    void createTable(Config config, StringBuilder log) {
        if (tableExist(config.tableName)) {
            log.append("table already exists: ").append(config.tableName).append("\n");
            return;
        }
        var sql = """
                create table public.${tableName}
                (
                    id         bigserial               not null
                        constraint ${tableName}_pk
                            primary key,
                    name      varchar(255)  default ''    not null,
                    created_at timestamp(0) default now() not null
                )
                """;
        sql = formatCode(sql, config);
        try {
            jdbcClient.sql(sql).update();
            log.append("table created: ").append(config.tableName).append("\n");
        } catch (Exception e) {
            LogUtils.error(getClass(), e);
            log.append("create table error: ").append(config.tableName).append("\n");
        }
    }

    boolean tableExist(String tableName) {
        return
                jdbcClient.sql("""
                                SELECT EXISTS (SELECT 1
                                               FROM information_schema.tables
                                               WHERE table_name = ?
                                                 AND table_schema = 'public')
                                """)
                        .param(tableName).query(Boolean.class).single();
    }

    void createMenu(Config config, StringBuilder log) {
        var example = Example.of(new AdminMenuEntity().setRoute("/" + config.businessNameVar));
        if (adminMenuRepository.exists(example)) {
            log.append("menu already exists: ").append(config.businessNameVar).append("\n");
            return;
        }
        AdminMenuEntity adminMenu = new AdminMenuEntity()
                .setParentId(config.moduleId)
                .setName(config.menuName)
                .setCache(true)
                .setType(2)
                .setVisible(true)
                .setPage(config.moduleName + "/" + config.businessName + "Page")
                .setRoute(DbUtils.camelToSnake(config.businessNameVar));
        adminMenuRepository.saveAndFlush(adminMenu);
        var menuId = adminMenu.getId();
        adminMenu = new AdminMenuEntity()
                .setParentId(menuId)
                .setType(3)
                .setName("查询" + config.menuName)
                .setRoute("query")
                .setOrderNum(10);
        adminMenuRepository.save(adminMenu);
        adminMenu = new AdminMenuEntity()
                .setParentId(menuId)
                .setType(3)
                .setName("添加" + config.menuName)
                .setRoute("add")
                .setOrderNum(20);
        adminMenuRepository.save(adminMenu);
        adminMenu = new AdminMenuEntity()
                .setParentId(menuId)
                .setType(3)
                .setName("修改" + config.menuName)
                .setRoute("edit")
                .setOrderNum(30);
        adminMenuRepository.save(adminMenu);
        adminMenu = new AdminMenuEntity()
                .setParentId(menuId)
                .setType(3)
                .setName("删除" + config.menuName)
                .setRoute("delete")
                .setOrderNum(40);
        adminMenuRepository.save(adminMenu);
        adminMenuCache.update();
        log.append("menu created: ").append(config.menuName).append("\n");
    }

    void createApiFile(Config config, StringBuilder log) {
        var apiTsPath = Path.of(config.frontendPath, "src", "api", config.moduleName + ".ts");
        try {
            Files.createDirectories(apiTsPath.getParent());
        } catch (IOException e) {
            log.append(e.getMessage()).append("\n");
            return;
        }
        if (!apiTsPath.toFile().exists()) {
            var ts = """
                    import request from '@/utils/system/request'
                    
                    export default ({
                    })
                    """;
            try {
                Files.writeString(apiTsPath, ts);
            } catch (IOException e) {
                log.append(e.getMessage()).append("\n");
                return;
            }
        }
        var fileTs = readFile(apiTsPath);
        if (fileTs.contains("get" + config.businessName + "List:")) {
            log.append("api ts already exists: ").append(config.businessNameVar).append("\n");
            return;
        }
        var templateStr = readFile(Path.of(config.templateDir, "api.ts.txt"));
        var tsStr = formatCode(templateStr, config);
        fileTs = Pattern.compile("^(.+)}\\s*\\)\\s*$", Pattern.DOTALL | Pattern.MULTILINE)
                .matcher(fileTs)
                .replaceAll(matchResult ->
                        matchResult.group(1).replaceAll("}\\s*$", "},")
                                + tsStr + "\r\n})");
        try {
            Files.writeString(apiTsPath, fileTs);
        } catch (IOException e) {
            log.append(e.getMessage()).append("\n");
            return;
        }
        log.append("api ts created: ").append(config.businessNameVar).append("\n");
    }


    record Config(
            String frontendPath,
            Long moduleId,
            String moduleName,
            String businessName,
            String businessNameVar,
            String menuName,
            String packagePrefix,
            String codeDir,
            String templateDir,
            String tableName
    ) {
        @NonNull
        public static Config of(GenDto genDto) throws Exception {
            var adminMenuRepository = AppConfig.getContext().getBean(AdminMenuRepository.class);
            var menuEntity = adminMenuRepository.findById(genDto.moduleId()).orElse(null);
            if (menuEntity == null) {
                throw new Exception("模块不存在, moduleId: " + genDto.moduleId());
            }
            var moduleName = menuEntity.getRoute().substring(1);
            var businessName = genDto.businessName();
            businessName = businessName.substring(0, 1).toUpperCase() + businessName.substring(1);
            var businessNameVar = businessName.substring(0, 1).toLowerCase() + businessName.substring(1);
            var packagePrefix = getPackagePrefix();
            var codeDir = Path.of(AppConfig.getAppDir(), "src", "main", "java", packagePrefix.replace('.', File.separatorChar)).toString();
            var templateDir = Path.of(AppConfig.getAppDir(), "src", "main", "resources", "templates", "devel").toString();

            if (!Files.isDirectory(Path.of(codeDir))) {
                throw new Exception("代码目录不存在, codeDir: " + codeDir);
            }

            if (!Files.isDirectory(Path.of(templateDir))) {
                throw new Exception("模板目录不存在, templateDir: " + templateDir);
            }
            var tableName = "t_" + moduleName + "_" + businessName.toLowerCase();
            return new Config(
                    genDto.frontendPath(),
                    genDto.moduleId(), moduleName, businessName,
                    businessNameVar, genDto.menuName(),
                    packagePrefix, codeDir, templateDir, tableName);
        }

        static String getPackagePrefix() {
            var arr = Config.class.getPackage().getName().split("\\.");
            StringBuilder result = new StringBuilder(arr[0]);
            for (int i = 1; i < arr.length - 3; i++) {
                result.append(".").append(arr[i]);
            }
            return result.toString();
        }

        @NonNull
        @Override
        public String toString() {
            return "packagePrefix: " + packagePrefix
                    + "  moduleName: " + moduleName
                    + "   businessName: " + businessName
                    + "   tableName: " + tableName + "\n"
                    + "codeDir: " + codeDir + "\n"
                    + "templateDir: " + templateDir + "\n";
        }
    }

}
