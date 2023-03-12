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

    TrpcFastjsonSerializer((byte) 5, "TrpcFastjsonSerializer");

    private final byte code;
    private final String name;


}
