package tong.trpc.core.io.serialize;

import java.util.concurrent.ConcurrentHashMap;

public class TrpcSerializerManager {

    private final static ConcurrentHashMap<Byte, ITrpcSerializer> type2Serializer = new ConcurrentHashMap<>();

    static {
        ITrpcSerializer[] serializers = new ITrpcSerializer[]{ new TrpcJdkSerializer(), new TrpcProtostuffSerializer(),
            new TrpcKryoSerializer(), new TrpcFstSerializer(), new TrpcFastjsonSerializer()
        };
        for (ITrpcSerializer serializer : serializers) {
            type2Serializer.put(serializer.getType(), serializer);
        }
    }

    public static ITrpcSerializer getSerializer(byte key) {
        ITrpcSerializer iSerializer = type2Serializer.get(key);
        if (iSerializer == null) {
            return new TrpcJdkSerializer();
        }
        return iSerializer;
    }



}
