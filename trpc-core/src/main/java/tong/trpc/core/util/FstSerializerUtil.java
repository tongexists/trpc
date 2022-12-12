package tong.trpc.core.util;

import org.nustaq.serialization.FSTConfiguration;


/**
 * @Author tong-exists
 * @Create 2022/12/12 18:54
 * @Version 1.0
 */
public class FstSerializerUtil {

    static ThreadLocal<FSTConfiguration> confs = new ThreadLocal(){
        @Override
        public FSTConfiguration initialValue() {
            return FSTConfiguration.createDefaultConfiguration();
        }
    };

    private static FSTConfiguration getFST(){
        return confs.get();
    }


    /**
     * 将指定对象序列化为byte数组
     * @param obj
     * @return
     */
    public static <T> byte[] serialize(T obj) {
        return getFST().asByteArray(obj);
    }

    /**
     * 将byte数组反序列化为指定对象
     * @param sec
     * @return
     */
    public static <T> T deserialize(byte[] sec) {
        return (T)getFST().asObject(sec);
    }

}
