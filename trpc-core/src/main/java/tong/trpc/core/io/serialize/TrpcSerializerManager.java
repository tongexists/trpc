package tong.trpc.core.io.serialize;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 序列化器管理
 */
public class TrpcSerializerManager {
    /**
     * 保存类型代码 -》 序列化器的关系
     */
    private final static ConcurrentHashMap<Byte, ITrpcSerializer> type2Serializer = new ConcurrentHashMap<>();

    static {
        // 默认序列化器放进去
        ITrpcSerializer[] serializers = new ITrpcSerializer[]{ new TrpcJdkSerializer(), new TrpcProtostuffSerializer(),
            new TrpcKryoSerializer(), new TrpcFstSerializer(), new TrpcFastjsonSerializer()
        };
        for (ITrpcSerializer serializer : serializers) {
            type2Serializer.put(serializer.getType(), serializer);
        }
    }

    /**
     * 添加序列化器
     * @param serializer 序列化器
     */
    public static void addSerializer(ITrpcSerializer serializer) {
        type2Serializer.put(serializer.getType(), serializer);
    }

    /**
     * 根据类型代码，获取序列化器
     * @param key 型代码
     * @return 序列化器
     */

    public static ITrpcSerializer getSerializer(byte key) {
        ITrpcSerializer iSerializer = type2Serializer.get(key);
        if (iSerializer == null) {
            return new TrpcJdkSerializer();
        }
        return iSerializer;
    }



}
