package tong.trpc.core.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author tong-exists
 * @Create 2022/12/12 20:31
 * @Version 1.0
 */
public class GsonSerializerUtil {



    private static final Map<Class<?>, List<Object>> typeAdapters = new HashMap<>();

    public static void addTypeAdapter(Class<?> clazz, List<Object> list) {
        typeAdapters.put(clazz, list);
    }

    /**
     * 将字符串转换为Object对象
     *
     * @param jsonObj
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T fromJsonToObject(String jsonObj, Class<T> clazz) {
        GsonBuilder builder = new GsonBuilder();
        for (Map.Entry<Class<?>, List<Object>> entry : typeAdapters.entrySet()) {
            for (Object ad : entry.getValue()) {
                builder.registerTypeAdapter(entry.getKey(), ad);
            }
        }
        Gson gson = builder.create();
        return gson.fromJson(jsonObj, clazz);
    }


    /**
     * 将Object对象转换成为Json字符串
     *
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> String objectToJson(T obj) {
        GsonBuilder builder = new GsonBuilder();
        for (Map.Entry<Class<?>, List<Object>> entry : typeAdapters.entrySet()) {
            for (Object ad : entry.getValue()) {
                builder.registerTypeAdapter(entry.getKey(), ad);
            }
        }
        Gson gson = builder.create();
        return gson.toJson(obj);
    }
}
