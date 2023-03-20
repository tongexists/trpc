package tong.trpc.core.filter.client;

import tong.trpc.core.domain.TrpcRequestHolder;
import tong.trpc.core.domain.TrpcTransportProtocol;
import tong.trpc.core.domain.request.TrpcRequest;
import tong.trpc.core.domain.response.TrpcResponse;
import tong.trpc.core.io.TrpcClient;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * 发送请求过滤器
 * @Author tong-exists
 * @Create 2023/3/20 8:50
 * @Version 1.0
 */
public class TrpcSendRequestFilter implements TrpcClientFilter{
    /**
     * 发起请求
     * @param request 请求
     * @param future 响应的CompletableFuture
     * @param chain 过滤器链
     */
    @Override
    public void doFilter(TrpcRequest request, CompletableFuture<TrpcResponse> future, TrpcClientFilterChain chain) {
        TrpcTransportProtocol protocol = TrpcRequestHolder.PROTOCOL_MAP.get(request.getRequestId());
        if (protocol==null) {
            throw new RuntimeException(String.format("TrpcRequestHolder.PROTOCOL_MAP中未设置%s对应的protocol", request.getRequestId()));
        }
        // 发起请求
        TrpcClient.sendRequest(protocol,
                request.getServiceInstanceName());
        chain.doFilter(request, future);
    }

    @Override
    public boolean isEnable() {
        return true;
    }

    @Override
    public String order() {
        return TrpcClientFiltersOrder.TrpcSendRequestFilter.getOrder();
    }
}
