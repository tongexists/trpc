package tong.trpc.core.filter.client;

import tong.trpc.core.domain.request.TrpcRequest;
import tong.trpc.core.domain.response.TrpcResponse;

import java.util.concurrent.CompletableFuture;

/**
 * 客户端请求过滤器接口
 * @Author tong-exists
 * @Create 2023/3/16 13:04
 * @Version 1.0
 */
public interface TrpcClientFilter {
    /**
     * 执行过滤
     * @param request 请求
     * @param future 响应的CompletableFuture
     * @param chain 过滤器链
     */
    void doFilter(TrpcRequest request, CompletableFuture<TrpcResponse> future, TrpcClientFilterChain chain);

    /**
     * 是否开启
     * @return
     */
    boolean isEnable();

    /**
     * 顺序，决定了在过滤器链中的位置，按字典升序，请使用数字
     * 例如 0, 1, 10, 11, 2
     * @return 顺序
     */
    String order();

}
