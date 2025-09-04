package my.iris.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * json 助手类
 */
@Component
public final class JsonUtils {
    private static ObjectMapper mapper;

    public JsonUtils(ObjectMapper objectMapper) {
        mapper = objectMapper;
    }

    public static <T> T clone(T value) {
        if (value == null) {
            return null;
        }
        try {
            return mapper.readValue(
                    mapper.writeValueAsString(value),
                    mapper.constructType(value.getClass())
            );
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    /**
     * json字符串转对象实列
     *
     * @param str    json字符串
     * @param tClass 实例的类型
     * @param <T>    泛型
     * @return 转换的对象实例，失败返回null
     */
    public static <T> T parse(String str, Class<T> tClass) {
        try {
            return mapper.readValue(str, tClass);
        } catch (Exception ignored) {
        }
        return null;
    }


    /**
     * json字符串转换为指定类实例的List
     *
     * @param str    json字符串
     * @param tClass 实例的类型
     * @param <T>    泛型
     * @return 转换后的List, 失败返回null
     */
    public static <T> List<T> parseList(String str, Class<T> tClass) {
        return parseList(str, tClass, false);
    }

    public static <T> List<T> parseList(String str, Class<T> tClass, boolean returnEmptyIfNull) {
        List<T> list = null;
        try {
            list = mapper.readValue(str, mapper.getTypeFactory().constructParametricType(List.class, tClass));
        } catch (JsonProcessingException ignored) {
        }
        if (list == null && returnEmptyIfNull) {
            list = new ArrayList<>();
        }
        return list;
    }

    /**
     * 对象序列化为 json字符串
     *
     * @param value 要转换成 json 的实列
     * @return json字符串, 失败返回null
     */
    public static String stringify(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException ignored) {
        }
        return null;
    }

}
