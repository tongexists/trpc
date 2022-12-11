package tong.trpc.core.io.serialize;

import io.protostuff.LinkBuffer;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * @Author tong-exists
 * @Create 2022/12/11 20:27
 * @Version 1.0
 */
@Slf4j
public class TrpcProtostuffSerializer implements ITrpcSerializer{

    private ThreadLocal<LinkedBuffer> linkedBufferThreadLocal = new ThreadLocal<>();
    @Override
    public <T> byte[] serialize(T obj) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
        log.info("TrpcProtostuffSerializer serialize...");
        Schema<T> schema = RuntimeSchema.getSchema((Class<T>)obj.getClass());
        LinkedBuffer buffer = linkedBufferThreadLocal.get();
        if (buffer == null) {
            buffer = LinkedBuffer.allocate(1024);
            linkedBufferThreadLocal.set(buffer);
        }
        final byte[] protostuff;
        try {
            protostuff = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
            return protostuff;
        }
        finally {
            buffer.clear();
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException, ClassNotFoundException {
        log.info("TrpcProtostuffSerializer deserialize...");
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        T obj = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(data, obj, schema);
        return obj;
    }

    @Override
    public byte getType() {
        return TrpcSerialType.TrpcProtostuffSerializer.getCode();
    }
}
