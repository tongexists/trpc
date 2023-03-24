package tong.trpc.core.filter.client;

import lombok.extern.slf4j.Slf4j;
import tong.trpc.core.domain.TrpcFutureDecorator;
import tong.trpc.core.domain.TrpcRequestHolder;
import tong.trpc.core.domain.TrpcResponseCode;
import tong.trpc.core.domain.request.TrpcRequest;
import tong.trpc.core.domain.response.TrpcResponse;
import tong.trpc.core.domain.response.TrpcResponseImpl;

import java.util.concurrent.CompletableFuture;

/**
 * 客户端的异常处理器
 * @Author tong-exists
 * @Create 2023/3/18 13:18
 * @Version 1.0
 */
@Slf4j
public class TrpcClientExceptionHandlerFilter implements TrpcClientFilter {
    /**
     * 遇到异常，让CompletableFuture以异常结束
     * @param request
     * @param chain
     */
    @Override
    public TrpcResponse doFilter(TrpcRequest request, TrpcClientFilterChain chain) {
        try {
            TrpcResponse response = chain.doFilter(request);

            return response;
        } catch (Exception e) {
            log.error("",e);
            long requestId = request.getRequestId();
            TrpcFutureDecorator trpcFutureDecorator = TrpcRequestHolder.REQUEST_MAP.get(requestId);
            if (trpcFutureDecorator != null && trpcFutureDecorator.getResponseFuture() != null) {
                trpcFutureDecorator.getResponseFuture().completeExceptionally(e);
            }
            TrpcResponse response = new TrpcResponseImpl();
            response.setCode(TrpcResponseCode.ERROR.getCode());
            response.setMsg("本地客户端遇到异常："+e.getMessage());
            return response;
        }
    }

    @Override
    public boolean isEnable() {
        return true;
    }

    @Override
    public String order() {
        return TrpcClientFiltersOrder.TrpcClientExceptionHandlerFilter.getOrder();
    }
}
