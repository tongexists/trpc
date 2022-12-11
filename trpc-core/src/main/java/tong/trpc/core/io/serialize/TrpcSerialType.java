package tong.trpc.core.io.serialize;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Administrator
 */

@AllArgsConstructor
@Getter
public enum TrpcSerialType {
    TrpcJdkSerializer((byte) 0, "TrpcJdkSerializer"),
    TrpcProtostuffSerializer((byte) 1, "TrpcProtostuffSerializer");
    private final byte code;
    private final String name;


}
