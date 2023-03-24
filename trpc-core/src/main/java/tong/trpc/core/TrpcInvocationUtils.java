package tong.trpc.core;

import brave.Span;
import brave.propagation.Propagation;
import brave.propagation.TraceContext;
import tong.trpc.core.TrpcConfig;
import tong.trpc.core.domain.*;
import tong.trpc.core.domain.request.TrpcRequest;
import tong.trpc.core.domain.response.TrpcResponse;
import tong.trpc.core.filter.client.TrpcClientFilters;
import tong.trpc.core.invocation.TrpcInvocation;
import tong.trpc.core.io.TrpcClient;
import tong.trpc.core.io.serialize.TrpcSerialType;
import tong.trpc.core.zipkin.ZipkinHolder;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

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
        TrpcTransportProtocolHeader header = new TrpcTransportProtocolHeader(
                TrpcConstant.MAGIC, TrpcSerialType.TrpcKryoSerializer.getCode(), TrpcMessageType.REQUEST.getCode(),
                request.getRequestId(), 0);
        TrpcTransportProtocolBody<TrpcRequest> body = new TrpcTransportProtocolBody<>(request);
        TrpcTransportProtocol<TrpcRequest> requestProtocol = new TrpcTransportProtocol<>();
        requestProtocol.setHeader(header);
        requestProtocol.setBody(body);
        return invokeByProtocol(requestProtocol);
    }

    /**
     * 发情远程调用
     * @param protocol 协议
     * @return CompletableFuture<TrpcResponse>
     */
    public static CompletableFuture<TrpcResponse> invokeByProtocol(TrpcTransportProtocol<TrpcRequest> protocol) {
        injectTraceContext(protocol);
        long requestId = checkRequestId(protocol);
        CompletableFuture<TrpcResponse> responseFuture = new CompletableFuture<>();

        //请求对应的responseFuture
        TrpcRequestHolder.REQUEST_MAP.put(requestId, new TrpcFutureDecorator(responseFuture));
        TrpcRequestHolder.PROTOCOL_MAP.put(requestId, protocol);
        final long finalRequestId = requestId;

        CompletableFuture<TrpcResponse> ret = new CompletableFuture<>();
        ret.whenComplete(new BiConsumer<TrpcResponse, Throwable>() {
            @Override
            public void accept(TrpcResponse trpcResponse, Throwable throwable) {
                TrpcRequestHolder.PROTOCOL_MAP.remove(finalRequestId);
                TrpcRequestHolder.REQUEST_MAP.remove(finalRequestId);
            }
        });
        TrpcConfig.getClientFiltersThreadPool().execute(() -> {
            //已处理
            TrpcResponse response = TrpcClientFilters.doFilter(protocol.getBody().getContent());
            if (response.getCode() == TrpcResponseCode.SUCCESS.getCode()) {
                ret.complete(response);
            } else {
                ret.completeExceptionally(new RuntimeException(response.getMsg()));
            }
        });
        return ret;
    }


    /**
     * 获取请求id
     * @return
     */
    public static long newRequestId() {
        return TrpcRequestHolder.REQUEST_ID.incrementAndGet();
    }

    /**
     * 注入追踪信息到请求
     * @param protocol
     */
    private static void injectTraceContext(TrpcTransportProtocol<TrpcRequest> protocol) {
        TraceContext currentThreadTraceContext = ZipkinHolder.traceContextThreadLocal.get();
        TrpcRequest request = protocol.getBody().getContent();
        TraceContext.Injector<TrpcRequest> injector = ZipkinHolder.rpcTracing.propagation().injector(new Propagation.RemoteSetter<TrpcRequest>() {
            @Override
            public Span.Kind spanKind() {
                return Span.Kind.CLIENT;
            }

            @Override
            public void put(TrpcRequest request, String fieldName, String value) {
                request.getAttributes().put(fieldName, value);
            }
        });
        injector.inject(currentThreadTraceContext, request);
    }

    /**
     * 检查协议头的请求id和请求的id是否一致
     * @param protocol
     * @return 请求id
     */
    private static long checkRequestId(TrpcTransportProtocol<TrpcRequest> protocol) {
        long requestId = 0;
        if (protocol.getHeader().getRequestId() == 0L){
            if (protocol.getBody().getContent().getRequestId() == 0L) {
                throw new RuntimeException("未设置requestId");
            } else {
                requestId = protocol.getBody().getContent().getRequestId();
                protocol.getHeader().setRequestId(requestId);
            }
        } else {
            if (protocol.getBody().getContent().getRequestId() == 0L) {
                requestId = protocol.getHeader().getRequestId();
                protocol.getBody().getContent().setRequestId(requestId);
            } else {
                if (protocol.getHeader().getRequestId() != protocol.getBody().getContent().getRequestId()) {
                    throw new RuntimeException("协议头部中的requestId和请求中的requestId不一致");
                } else {
                    requestId = protocol.getHeader().getRequestId();
                }
            }
        }
        return requestId;
    }




}
