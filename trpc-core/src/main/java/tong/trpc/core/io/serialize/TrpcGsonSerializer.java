package tong.trpc.core.io.serialize;

import tong.trpc.core.domain.TrpcRequest;
import tong.trpc.core.domain.TrpcResponse;
import tong.trpc.core.io.serialize.gson.TrpcRequestGsonDeserializer;
import tong.trpc.core.io.serialize.gson.TrpcResponseGsonDeserializer;
import tong.trpc.core.util.GsonSerializerUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @Author tong-exists
 * @Create 2022/12/13 21:30
 * @Version 1.0
 */
public class TrpcGsonSerializer implements ITrpcSerializer{

    {
        System.out.println("TrpcGsonSerializerTrpcGsonSerializerTrpcGsonSerializer");
        GsonSerializerUtil.addTypeAdapter(TrpcRequest.class, Arrays.asList(new TrpcRequestGsonDeserializer()));
        GsonSerializerUtil.addTypeAdapter(TrpcResponse.class, Arrays.asList(new TrpcResponseGsonDeserializer()));
    }

    @Override
    public <T> byte[] serialize(T obj) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
        return GsonSerializerUtil.objectToJson(obj).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException, ClassNotFoundException {
        return GsonSerializerUtil.fromJsonToObject(new String(data, StandardCharsets.UTF_8), clazz);
    }

    @Override
    public byte getType() {
        return TrpcSerialType.TrpcGsonSerializer.getCode();
    }
}
