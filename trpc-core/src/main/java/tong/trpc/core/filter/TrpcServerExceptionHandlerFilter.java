package tong.trpc.core.filter;

import lombok.extern.slf4j.Slf4j;
import tong.trpc.core.domain.TrpcResponseCode;
import tong.trpc.core.domain.request.TrpcRequest;
import tong.trpc.core.domain.response.TrpcResponse;

/**
 * @Author tong-exists
 * @Create 2023/3/18 13:18
 * @Version 1.0
 */
@Slf4j
public class TrpcServerExceptionHandlerFilter implements TrpcServerFilter {
    @Override
    public void doFilter(TrpcRequest request, TrpcResponse response, TrpcServerFilterChain chain) {
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            log.error("",e);
            response.setCode(TrpcResponseCode.ERROR.getCode());
            response.setRequestId(request.getRequestId());
            response.setMsg(e.getMessage());
        }
    }
}
