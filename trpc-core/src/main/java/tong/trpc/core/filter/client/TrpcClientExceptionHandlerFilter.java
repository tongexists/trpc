package tong.trpc.core.filter.client;

import lombok.extern.slf4j.Slf4j;
import tong.trpc.core.domain.request.TrpcRequest;
import tong.trpc.core.domain.response.TrpcResponse;

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
     * @param future
     * @param chain
     */
    @Override
    public void doFilter(TrpcRequest request, CompletableFuture<TrpcResponse> future, TrpcClientFilterChain chain) {
        try {
            chain.doFilter(request, future);
        } catch (Exception e) {
            log.error("",e);
            future.completeExceptionally(e);
        }
    }
}
