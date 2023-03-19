package tong.trpc.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Trpc消息类型
 * @author tong-exists
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum TrpcMessageType {
    /**
     * 请求
     */
    REQUEST((byte) 1),
    /**
     * 响应
     */
    RESPONSE((byte) 2),
    /**
     * 心跳
     */
    HEARTBEAT((byte) 3);

    /**
     * 类型代码
     */
    private byte code;

    /**
     * 根据类型代码找TrpcMessageType
     * @param code 类型代码
     * @return TrpcMessageType
     */
    public static TrpcMessageType findByCode(int code) {
        for (TrpcMessageType value : TrpcMessageType.values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }

}
