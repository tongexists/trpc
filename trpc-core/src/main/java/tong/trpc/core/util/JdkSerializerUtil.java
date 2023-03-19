package tong.trpc.core.util;

import java.io.*;

/**
 * Jdk序列化工具
 * @Author tong-exists
 * @Create 2022/12/12 16:40
 * @Version 1.0
 */
public class JdkSerializerUtil {

    public static  <T> byte[] serialize(T obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        return bos.toByteArray();
    }


    public static <T> T deserialize(byte[] data, Class<T> clazz) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        return (T) ois.readObject();
    }
}
