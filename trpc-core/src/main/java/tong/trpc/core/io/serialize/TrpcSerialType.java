package tong.trpc.core.io.serialize;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 序列化类型枚举
 * @author Administrator
 */


@Getter
@AllArgsConstructor
public enum TrpcSerialType {
    /**
     * jdk
     */
    TrpcJdkSerializer((byte) 0, "TrpcJdkSerializer"),
    TrpcProtostuffSerializer((byte) 1, "TrpcProtostuffSerializer"),
    TrpcKryoSerializer((byte) 2, "TrpcKryoSerializer"),
    TrpcFstSerializer((byte) 3, "TrpcFstSerializer"),

    TrpcFastjsonSerializer((byte) 5, "TrpcFastjsonSerializer");
    /**
     * 类型代码
     */
    private final byte code;
    /**
     * 名字
     */
    private final String name;


}
