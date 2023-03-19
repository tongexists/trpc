package tong.trpc.core.domain;

import lombok.Getter;

/**
 * 响应类型
 * @Author tong-exists
 * @Create 2023/3/17 10:36
 * @Version 1.0
 */
@Getter
public enum TrpcResponseType {
    /**
     * 对应TrpcResponseImpl
     */
    TRPC_RESPONSE_IMPL(0, "TRPC_RESPONSE_IMPL"),
    /**
     * 对应TrpcMultipleResponse
     */
    TRPC_MULTIPLE_RESPONSE(1, "TRPC_MULTIPLE_RESPONSE");

    /**
     * 代码
     */
    private int code;
    /**
     * 描述
     */
    private String description;

    private TrpcResponseType(int code, String description) {
        this.code = code;
        this.description = description;
    }
}
