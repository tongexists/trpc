package tong.trpc.core.filter;

import tong.trpc.core.domain.TrpcRequest;
import tong.trpc.core.domain.TrpcResponse;

import java.util.concurrent.CompletableFuture;

/**
 * @Author tong-exists
 * @Create 2023/3/16 13:04
 * @Version 1.0
 */
public interface TrpcClientFilter {

    void doFilter(TrpcRequest request, CompletableFuture<TrpcResponse> future, TrpcClientFilterChain chain);

}
