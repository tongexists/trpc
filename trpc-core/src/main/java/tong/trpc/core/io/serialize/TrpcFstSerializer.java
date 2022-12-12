package tong.trpc.core.io.serialize;

import tong.trpc.core.util.FstSerializerUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * @Author tong-exists
 * @Create 2022/12/12 20:06
 * @Version 1.0
 */
public class TrpcFstSerializer implements ITrpcSerializer{
    @Override
    public <T> byte[] serialize(T obj) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
        return FstSerializerUtil.serialize(obj);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException, ClassNotFoundException {
        return FstSerializerUtil.deserialize(data);
    }

    @Override
    public byte getType() {
        return TrpcSerialType.TrpcFstSerializer.getCode();
    }
}
