package my.iris.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Table;
import lombok.Getter;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * database utils
 */
@Component
public final class DbUtils {
    @Getter
    private static JdbcClient jdbcClient;
    private static EntityManager entityManager;

    public DbUtils(JdbcClient jdbcClient, EntityManager entityManager) {
        DbUtils.jdbcClient = jdbcClient;
        DbUtils.entityManager = entityManager;
    }

    /**
     * 将驼峰命名转换为下划线命名
     * like ''userName'' to ''user_name''
     */
    public static String camelToSnake(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        // 正则表达式匹配
        Pattern pattern = Pattern.compile("([a-z0-9])([A-Z])");
        Matcher matcher = pattern.matcher(str);
        return matcher.replaceAll("$1_$2").toLowerCase();
    }

    public static void commit() {
        jdbcClient.sql("COMMIT").update();
    }

    public static long getIdSeq(Class<?> entityClass) {
        var seqName = getTableName(entityClass) + "_id_seq";
        return Helper.parseNumber(
                jdbcClient.sql("SELECT CURRVAL(?)").param(seqName).query().singleRow().get("currval"),
                Long.class);
    }


    /**
     * delete row by primary key
     *
     * @param entity table entity
     */
    public static void delete(Object entity) {
        entityManager.remove(entity);
    }


    public static <T> T findById(Class<T> entityClass, Object id) {
        return entityManager.find(entityClass, id);
    }

    public static <T> T findByIdForUpdate(Class<T> entityClass, Object id) {
        return entityManager.find(entityClass, id, LockModeType.PESSIMISTIC_WRITE);
    }

    /**
     * 构造 SQL LIKE 语句的通配符格式
     *
     * @param words words
     * @return like words
     */
    public static String getLikeWords(String words) {
        if (StringUtils.hasText(words)) {
            words = words.trim()
                    .replace("%", "\\%")
                    .replace("_", "\\_");
            return "%" + words + "%";
        }
        return null;
    }

    /**
     * get sort
     *
     * @param sortableColumns sortable columns, [field:alias, field2, ...]
     * @param sortStr         sort param
     * @param defaultSortStr  default sort
     * @return sort
     */
    public static Sort getSort(String[] sortableColumns, String sortStr, String defaultSortStr) {
        if (!StringUtils.hasText(sortStr)) {
            sortStr = defaultSortStr;
        }
        var parts = sortStr.split(",");
        if (parts.length == 2) {
            String sortName = null;
            for (var item : sortableColumns) {
                var arr = item.split(":");
                if (arr.length == 1 && item.equals(parts[0])) {
                    sortName = item;
                    break; // use field
                } else if (arr.length > 1 && arr[0].equals(parts[0])) {
                    sortName = arr[1]; // use alias
                    break;
                }
            }
            if (sortName != null) {
                Sort.Direction direction = "desc".equals(parts[1])
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;
                return Sort.by(direction, sortName);
            }
        }
        return Sort.unsorted();
    }

    /**
     * get table name by entity
     *
     * @param entity table entity
     * @return table name
     */
    public static String getTableName(Class<?> entity) {
        Table annotation = entity.getAnnotation(Table.class);
        if (annotation == null || !StringUtils.hasLength(annotation.name())) {
            throw new IllegalArgumentException("entity has not table name");
        }
        return annotation.name();
    }

    /**
     * rollback
     */
    public static void rollback() {
        jdbcClient.sql("ROLLBACK").update();
    }

    /**
     * 将下划线命名转换为驼峰命名
     */
    public static String snakeToCamel(String name) {
        if (name == null) {
            return null;
        }
        name = name.toLowerCase();
        StringBuilder sb = new StringBuilder(name);
        for (int i = 2; i < sb.length(); ) {
            if (sb.charAt(i - 1) == '_') {
                sb.replace(i - 1, i + 1, String.valueOf(sb.charAt(i)).toUpperCase());
            } else {
                i++;
            }
        }
        return sb.toString();
    }
}
