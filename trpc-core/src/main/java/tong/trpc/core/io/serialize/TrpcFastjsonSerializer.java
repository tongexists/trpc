package tong.trpc.core.io.serialize;

import tong.trpc.core.util.FastjsonSerializerUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;

/**
 * Fastjson序列化实现
 * @Author tong-exists
 * @Create 2022/12/13 20:45
 * @Version 1.0
 */
public class TrpcFastjsonSerializer implements ITrpcSerializer{
    @Override
    public byte[] serialize(Object obj) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
        return FastjsonSerializerUtil.objectToJson(obj).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException, ClassNotFoundException {
        return FastjsonSerializerUtil.fromJsonToObject(new String(data, StandardCharsets.UTF_8), clazz);
    }

    @Override
    public byte getType() {
        return TrpcSerialType.TrpcFastjsonSerializer.getCode();
    }
}
