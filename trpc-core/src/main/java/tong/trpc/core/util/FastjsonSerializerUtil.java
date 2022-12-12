package tong.trpc.core.util;


import com.alibaba.fastjson2.JSON;

/**
 * @Author tong-exists
 * @Create 2022/12/12 20:31
 * @Version 1.0
 */
public class FastjsonSerializerUtil {

    /**
     * 将字符串转换为Object对象
     *
     * @param jsonObj
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T fromJsonToObject(String jsonObj, Class<T> clazz) {
        return JSON.parseObject(jsonObj, clazz);
    }


    /**
     * 将Object对象转换成为Json字符串
     *
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> String objectToJson(T obj) {
        return JSON.toJSONString(obj);
    }
}
