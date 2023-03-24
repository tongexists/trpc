package tong.trpc.core.filter.client;

import tong.trpc.core.domain.TrpcRequestHolder;
import tong.trpc.core.domain.TrpcTransportProtocol;
import tong.trpc.core.domain.request.TrpcRequest;
import tong.trpc.core.domain.response.TrpcResponse;
import tong.trpc.core.io.TrpcClient;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

/**
 * 发送请求过滤器
 * @Author tong-exists
 * @Create 2023/3/20 8:50
 * @Version 1.0
 */
public class TrpcSendRequestFilter implements TrpcClientFilter{
    /**
     * 需要关注的是TrpcSendRequestFilter，他是具体发送请求的过滤器，
     * TrpcSendRequestFilter发送请求并同步获取响应，
     * 不会交给下个过滤器处理，因此即使将过滤器放在TrpcSendRequestFilter之后也无任何效果
     * @param request 请求
     * @param chain 过滤器链
     */
    @Override
    public TrpcResponse doFilter(TrpcRequest request, TrpcClientFilterChain chain) {
        TrpcTransportProtocol protocol = TrpcRequestHolder.PROTOCOL_MAP.get(request.getRequestId());
        if (protocol==null) {
            throw new RuntimeException(String.format("TrpcRequestHolder.PROTOCOL_MAP中未设置%s对应的protocol", request.getRequestId()));
        }
        // 发起请求
        TrpcClient.sendRequest(protocol,
                request.getServiceInstanceName());

        TrpcResponse trpcResponse = null;
        try {
            trpcResponse = TrpcRequestHolder.REQUEST_MAP.get(request.getRequestId()).getResponseFuture().get();
            return trpcResponse;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
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
