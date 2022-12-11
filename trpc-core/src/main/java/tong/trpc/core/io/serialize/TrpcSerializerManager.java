package tong.trpc.core.io.serialize;

import java.util.concurrent.ConcurrentHashMap;

public class TrpcSerializerManager {

    private final static ConcurrentHashMap<Byte, ITrpcSerializer> type2Serializer = new ConcurrentHashMap<>();

    static {
        ITrpcSerializer jdk = new TrpcJdkSerializer();
        type2Serializer.put(jdk.getType(), jdk);
        ITrpcSerializer protostuffSerializer = new TrpcProtostuffSerializer();
        type2Serializer.put(protostuffSerializer.getType(), protostuffSerializer);
    }

    public static ITrpcSerializer getSerializer(byte key) {
        ITrpcSerializer iSerializer = type2Serializer.get(key);
        if (iSerializer == null) {
            return new TrpcJdkSerializer();
        }
        return iSerializer;
    }



}
