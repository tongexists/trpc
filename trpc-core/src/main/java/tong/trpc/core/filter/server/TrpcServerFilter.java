package tong.trpc.core.filter.server;

import tong.trpc.core.domain.request.TrpcRequest;
import tong.trpc.core.domain.response.TrpcResponse;

/**
 * 服务端过滤器接口
 * @Author tong-exists
 * @Create 2023/3/16 13:04
 * @Version 1.0
 */
public interface TrpcServerFilter {

    /**
     * 执行过滤
     * @param request 请求
     * @param response 响应
     * @param chain 过滤器链
     */
    void doFilter(TrpcRequest request, TrpcResponse response, TrpcServerFilterChain chain);

}
