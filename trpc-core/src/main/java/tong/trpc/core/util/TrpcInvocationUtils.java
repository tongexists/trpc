package tong.trpc.core.util;

import tong.trpc.core.domain.*;
import tong.trpc.core.domain.request.TrpcRequest;
import tong.trpc.core.domain.response.TrpcResponse;
import tong.trpc.core.filter.client.TrpcClientFilters;
import tong.trpc.core.invocation.TrpcInvocation;
import tong.trpc.core.io.TrpcClient;
import tong.trpc.core.io.serialize.TrpcSerialType;

import java.util.concurrent.CompletableFuture;

/**
 * 调用工具类
 * @Author tong-exists
 * @Create 2023/3/17 10:14
 * @Version 1.0
 */
public class TrpcInvocationUtils {

    /**
     * 同步调用多个TrpcInvocation
     * @param invocations
     * @return
     */
    public static Object[] allSync(TrpcInvocation ...invocations) {
        Object[] ret = new Object[invocations.length];
        for (int i = 0; i < invocations.length; i++) {
            ret[i] = invocations[i].sync();
        }
        return ret;
    }

    /**
     * 发起远程调用
     * @param request 请求
     * @return  CompletableFuture<TrpcResponse>
     */
    public static CompletableFuture<TrpcResponse> invoke(TrpcRequest request){
        long requestId = TrpcRequestHolder.REQUEST_ID.incrementAndGet();
        TrpcTransportProtocolHeader header = new TrpcTransportProtocolHeader(
                TrpcConstant.MAGIC, TrpcSerialType.TrpcKryoSerializer.getCode(), TrpcMessageType.REQUEST.getCode(),
                requestId, 0);
        TrpcTransportProtocolBody<TrpcRequest> body = new TrpcTransportProtocolBody<>(request);
        TrpcTransportProtocol<TrpcRequest> requestProtocol = new TrpcTransportProtocol<>();
        requestProtocol.setHeader(header);
        requestProtocol.setBody(body);

        CompletableFuture<TrpcResponse> responseFuture = new CompletableFuture<>();
        //请求对应的responseFuture
        TrpcRequestHolder.REQUEST_MAP.put(requestId, new TrpcFutureDecorator(responseFuture));
        //已处理
        TrpcClientFilters.doFilter(request, responseFuture);
        // 发起请求
        TrpcClient.sendRequest(requestProtocol, request.getServiceInstanceName());
        return responseFuture;
    }

    /**
     * 发情远程调用
     * @param protocol 协议
     * @return CompletableFuture<TrpcResponse>
     */
    public static CompletableFuture<TrpcResponse> invokeByProtocol(TrpcTransportProtocol<TrpcRequest> protocol) {
        CompletableFuture<TrpcResponse> responseFuture = new CompletableFuture<>();
        //请求对应的responseFuture
        TrpcRequestHolder.REQUEST_MAP.put(protocol.getHeader().getRequestId(), new TrpcFutureDecorator(responseFuture));
        //已处理
        TrpcClientFilters.doFilter(protocol.getBody().getContent(), responseFuture);
        // 发起请求
        TrpcClient.sendRequest(protocol, protocol.getBody().getContent().getServiceInstanceName());
        return responseFuture;
    }



}
