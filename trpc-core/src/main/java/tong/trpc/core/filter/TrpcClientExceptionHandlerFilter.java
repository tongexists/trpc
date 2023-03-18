package tong.trpc.core.filter;

import lombok.extern.slf4j.Slf4j;
import tong.trpc.core.domain.TrpcResponseCode;
import tong.trpc.core.domain.request.TrpcRequest;
import tong.trpc.core.domain.response.TrpcResponse;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * @Author tong-exists
 * @Create 2023/3/18 13:18
 * @Version 1.0
 */
@Slf4j
public class TrpcClientExceptionHandlerFilter implements TrpcClientFilter {

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
