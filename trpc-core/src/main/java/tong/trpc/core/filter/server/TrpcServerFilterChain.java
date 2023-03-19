package tong.trpc.core.filter.server;

import tong.trpc.core.domain.request.TrpcRequest;
import tong.trpc.core.domain.response.TrpcResponse;

/**
 * 服务端过滤器链
 * @Author tong-exists
 * @Create 2023/3/16 13:12
 * @Version 1.0
 */

public class TrpcServerFilterChain {
    /**
     * 上一个执行过过滤的过滤器的索引
     */
    private int index = -1;
    /**
     * 过滤器数组
     */
    private TrpcServerFilter[] filters;

    /**
     * 构造
     * @param filters 过滤器数组
     */
    public TrpcServerFilterChain(TrpcServerFilter[] filters) {
        this.filters = filters;
    }

    /**
     * 取下一未执行过滤的过滤器，执行过滤。若未找到下一个则结束过滤
     * @param request 请求
     * @param response 响应的CompletableFuture
     */
    public void doFilter(TrpcRequest request, TrpcResponse response) {
        TrpcServerFilterChain chain = this;
        chain.index += 1;
        if (chain.index >= chain.filters.length) {
            return;
        }
        TrpcServerFilter curFilter = chain.filters[chain.index];
        curFilter.doFilter(request, response, chain);
    }





}
