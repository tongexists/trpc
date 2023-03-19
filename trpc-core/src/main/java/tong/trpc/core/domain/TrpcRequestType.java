package tong.trpc.core.domain;

import lombok.Getter;

/**
 * 请求类型枚举
 * @Author tong-exists
 * @Create 2023/3/17 10:36
 * @Version 1.0
 */
@Getter
public enum TrpcRequestType {
    /**
     * 对应TrpcRequestImpl
     */
    TRPC_REQUEST_IMPL(0, "TRPC_REQUEST_IMPL"),
    /**
     * 对应TrpcMultipleRequest
     */
    TRPC_MULTIPLE_REQUEST(1, "TRPC_MULTIPLE_REQUEST");

    /**
     * 类型代码
     */
    private int code;
    /**
     * 描述
     */
    private String description;

    private TrpcRequestType(int code, String description) {
        this.code = code;
        this.description = description;
    }
}
