package tong.trpc.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Trpc传输协议
 * @param <T> 响应体的内容类型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrpcTransportProtocol<T> implements Serializable {
    /**
     * 消息头
     */
    private TrpcTransportProtocolHeader header;
    /**
     * 消息体
     */
    private TrpcTransportProtocolBody<T> body;
}
