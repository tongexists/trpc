package tong.trpc.core.io.serialize;

import tong.trpc.core.util.JacksonSerializerUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;

/**
 * @Author tong-exists
 * @Create 2022/12/12 20:24
 * @Version 1.0
 */
public class TrpcJacksonSerializer implements ITrpcSerializer{
    @Override
    public <T> byte[] serialize(T obj) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
        return JacksonSerializerUtil.objectToJson(obj).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException, ClassNotFoundException {
        return JacksonSerializerUtil.jsonToObject(new String(data, StandardCharsets.UTF_8), clazz);
    }

    @Override
    public byte getType() {
        return TrpcSerialType.TrpcJacksonSerializer.getCode();
    }
}
