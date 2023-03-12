package tong.trpc.core.io.serialize;

import tong.trpc.core.util.JdkSerializerUtil;

import java.io.*;

public class TrpcJdkSerializer implements ITrpcSerializer {

    @Override
    public byte[] serialize(Object obj) throws IOException {
        return JdkSerializerUtil.serialize(obj);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws IOException, ClassNotFoundException {
        return JdkSerializerUtil.deserialize(data, clazz);
    }


    @Override
    public byte getType() {
        return TrpcSerialType.TrpcJdkSerializer.getCode();
    }
}
