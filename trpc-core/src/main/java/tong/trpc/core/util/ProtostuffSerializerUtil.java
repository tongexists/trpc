package tong.trpc.core.util;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Protostuff序列化工具
 * @Author tong-exists
 * @Create 2022/12/12 16:46
 * @Version 1.0
 */
public class ProtostuffSerializerUtil {

    private static final int LINKED_BUFFER_SIZE = 1024;
    private static ThreadLocal<LinkedBuffer> linkedBufferThreadLocal = new ThreadLocal() {
        @Override
        protected LinkedBuffer initialValue() {
            return LinkedBuffer.allocate(LINKED_BUFFER_SIZE);
        }
    };


    public static <T> byte[] serialize(T obj) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
        Schema<T> schema = RuntimeSchema.getSchema((Class<T>)obj.getClass());
        LinkedBuffer buffer = linkedBufferThreadLocal.get();
        final byte[] protostuff;
        try {
            protostuff = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
            return protostuff;
        }
        finally {
            buffer.clear();
        }
    }


    public static <T> T deserialize(byte[] data, Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException, ClassNotFoundException {
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        T obj = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(data, obj, schema);
        return obj;
    }
}
