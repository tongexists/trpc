package tong.trpc.core.domain;

import lombok.Getter;

/**
 * @Author tong-exists
 * @Create 2023/3/17 10:36
 * @Version 1.0
 */
@Getter
public enum TrpcResponseType {

    TRPC_RESPONSE(0, "TRPC_RESPONSE"),
    TRPC_MULTIPLE_RESPONSE(1, "TRPC_MULTIPLE_RESPONSE");


    private int code;

    private String description;

    private TrpcResponseType(int code, String description) {
        this.code = code;
        this.description = description;
    }
}
