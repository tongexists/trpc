package tong.trpc.core.domain.response;

import lombok.Data;

/**
 * 抽象响应
 * @Author tong-exists
 * @Create 2023/3/17 14:09
 * @Version 1.0
 */
@Data
public abstract class AbstractTrpcResponse implements TrpcResponse {
    /**
     * 返回的结果
     */
    private Object data;
    /**
     * 响应消息
     */
    private String msg;
    /**
     * 响应码
     */
    private int code;

    private long requestId; // 请求ID 8个字节
    /**
     * 响应类型TrpcResponseCode
     */
    private int responseType;

}
