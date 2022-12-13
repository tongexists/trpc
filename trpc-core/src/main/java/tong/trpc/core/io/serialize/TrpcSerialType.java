package tong.trpc.core.io.serialize;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Administrator
 */


@Getter
@AllArgsConstructor
public enum TrpcSerialType {
    TrpcJdkSerializer((byte) 0, "TrpcJdkSerializer"),
    TrpcProtostuffSerializer((byte) 1, "TrpcProtostuffSerializer"),
    TrpcKryoSerializer((byte) 2, "TrpcKryoSerializer"),
    TrpcFstSerializer((byte) 3, "TrpcFstSerializer"),
    TrpcJacksonSerializer((byte) 4, "TrpcJacksonSerializer"),
    TrpcFastjsonSerializer((byte) 5, "TrpcFastjsonSerializer"),
    TrpcGsonSerializer((byte) 6, "TrpcGsonSerializer");
    private final byte code;
    private final String name;


}
