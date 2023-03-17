package tong.trpc.core.domain.request;

import lombok.Data;
import tong.trpc.core.domain.response.TrpcMultipleResponse;
import tong.trpc.core.domain.response.TrpcResponse;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @Author tong-exists
 * @Create 2023/3/17 13:55
 * @Version 1.0
 */
@Data
public class TrpcMultipleRequest extends TrpcRequestImpl {

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
