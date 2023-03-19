package tong.trpc.core.util;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;

/**
 * Fastjson序列化工具
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
        return JSON.parseObject(jsonObj, clazz, JSONReader.Feature.SupportAutoType, JSONReader.Feature.SupportClassForName);
    }


    /**
     * 将Object对象转换成为Json字符串
     *
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> String objectToJson(T obj) {
        return JSON.toJSONString(obj, JSONWriter.Feature.WriteClassName, JSONWriter.Feature.NotWriteRootClassName);
    }


}
