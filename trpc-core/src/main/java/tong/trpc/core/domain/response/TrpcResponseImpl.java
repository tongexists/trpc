package tong.trpc.core.domain.response;

import lombok.*;

/**
 * 普通请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TrpcResponseImpl extends AbstractTrpcResponse {

    /**
     * 返回类型
     */
    private Class<?> returnType;

}
