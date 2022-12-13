package tong.trpc.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum TrpcRequestType {
    REQUEST((byte) 1),
    RESPONSE((byte) 2),
    HEARTBEAT((byte) 3);


    private byte code;

    public static TrpcRequestType findByCode(int code) {
        for (TrpcRequestType value : TrpcRequestType.values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }

}
