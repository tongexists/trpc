package tong.trpc.core.domain;

import lombok.Getter;

/**
 * @Author tong-exists
 * @Create 2023/3/17 10:36
 * @Version 1.0
 */
@Getter
public enum TrpcRequestType {

    TRPC_REQUEST_IMPL(0, "TRPC_REQUEST_IMPL"),
    TRPC_MULTIPLE_REQUEST(1, "TRPC_MULTIPLE_REQUEST");


    private int code;

    private String description;

    private TrpcRequestType(int code, String description) {
        this.code = code;
        this.description = description;
    }
}
