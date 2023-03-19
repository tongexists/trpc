package tong.trpc.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 协议体
 * @param <T> 协议体内容类型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrpcTransportProtocolBody<T> {
    /**
     * 内容
     */
    private T content;
}
