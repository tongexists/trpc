package tong.trpc.core.domain.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import tong.trpc.core.domain.response.TrpcMultipleResponse;
import tong.trpc.core.domain.response.TrpcResponse;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * 批量调用同一个方法的请求，传递多个入参数数组，适用于同一个方法要调用多次的情况
 * @Author tong-exists
 * @Create 2023/3/17 13:55
 * @Version 1.0
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)

public class TrpcMultipleRequest extends TrpcRequestImpl {
    /**
     * 入参数组
     */
    private Object[][] paramsArr;

    @Override
    public TrpcResponse newResponse() {
        return new TrpcMultipleResponse();
    }

    @Override
    public Object[] getParams() {
        return Arrays.stream(paramsArr).flatMap(arr -> Arrays.stream(arr)).toArray();
    }


}
