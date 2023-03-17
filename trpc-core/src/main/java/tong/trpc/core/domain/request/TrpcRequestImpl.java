package tong.trpc.core.domain.request;

import lombok.*;
import tong.trpc.core.domain.response.TrpcResponse;
import tong.trpc.core.domain.response.TrpcResponseImpl;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TrpcRequestImpl extends AbstractTrpcRequest {

    private String className; // 类名

    private String methodName; //请求目标方法名

    private Object[] params; // 请求参数

    private Class<?>[] paramsTypes; // 参数类型

    private Class<?> returnType;


    @Override
    public TrpcResponse newResponse() {
        return new TrpcResponseImpl();
    }
}
