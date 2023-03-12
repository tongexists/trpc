package tong.trpc.core.io.serialize;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public interface ITrpcSerializer {
    /*
   序列化接口
    */
    byte[] serialize(Object obj) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException;

    /*
    反序列化接口
     */
    <T> T deserialize(byte[] data, Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException, ClassNotFoundException;

    /*
    序列化类型
     */
    byte getType();

}
