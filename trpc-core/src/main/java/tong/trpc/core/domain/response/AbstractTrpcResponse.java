package tong.trpc.core.domain.response;

import lombok.Data;

/**
 * @Author tong-exists
 * @Create 2023/3/17 14:09
 * @Version 1.0
 */
@Data
public abstract class AbstractTrpcResponse implements TrpcResponse {
    private Object data;

    private String msg;

    private int code;

    private long requestId; // 请求ID 8个字节

    private int responseType;

}
