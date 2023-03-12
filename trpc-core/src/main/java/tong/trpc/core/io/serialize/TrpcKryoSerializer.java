package tong.trpc.core.io.serialize;

import lombok.extern.slf4j.Slf4j;
import tong.trpc.core.util.KryoSerializerUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * @Author tong-exists
 * @Create 2022/12/12 11:13
 * @Version 1.0
 */
@Slf4j
public class TrpcKryoSerializer implements ITrpcSerializer {


    @Override
    public byte[] serialize(Object obj) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
        return KryoSerializerUtil.serialize(obj);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException, ClassNotFoundException {
         return KryoSerializerUtil.deserialize(data, clazz);
    }


    @Override
    public byte getType() {
        return TrpcSerialType.TrpcKryoSerializer.getCode();
    }
}
