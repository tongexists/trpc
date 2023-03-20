package tong.trpc.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 协议体
 * @param <T> 协议体内容类型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrpcTransportProtocolBody<T> implements Serializable {
    /**
     * 内容
     */
    private T content;
}
