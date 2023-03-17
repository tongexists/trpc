package tong.trpc.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum TrpcMessageType {
    REQUEST((byte) 1),
    RESPONSE((byte) 2),
    HEARTBEAT((byte) 3);


    private byte code;

    public static TrpcMessageType findByCode(int code) {
        for (TrpcMessageType value : TrpcMessageType.values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }

}
