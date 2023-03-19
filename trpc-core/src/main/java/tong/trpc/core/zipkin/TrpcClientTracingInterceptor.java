package tong.trpc.core.zipkin;

import brave.Span;
import brave.Tracer;
import brave.propagation.CurrentTraceContext;
import brave.propagation.TraceContext;
import brave.rpc.RpcClientHandler;
import brave.rpc.RpcTracing;
import lombok.extern.slf4j.Slf4j;
import tong.trpc.core.domain.request.TrpcRequest;
import tong.trpc.core.domain.response.TrpcResponse;
import tong.trpc.core.filter.client.TrpcClientFilter;
import tong.trpc.core.filter.client.TrpcClientFilterChain;
import tong.trpc.core.util.FastjsonSerializerUtil;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * 客户端追踪拦截器
 * @Author tong-exists
 * @Create 2023/3/14 21:17
 * @Version 1.0
 */
@Slf4j
public class TrpcClientTracingInterceptor implements TrpcClientFilter {


    private final Tracer tracer;
    /**
     * zipkin Rpc客户端处理器
     */
    private final RpcClientHandler handler;
    /**
     * 持有RpcTracing
     */
    private RpcTracing rpcTracing;

    /**
     * 构造
     * @param rpcTracing
     */
    public TrpcClientTracingInterceptor(RpcTracing rpcTracing) {
        tracer = rpcTracing.tracing().tracer();
        handler = RpcClientHandler.create(rpcTracing);
        this.rpcTracing = rpcTracing;
    }

    /**
     * 先从ZipkinHolder.traceContextThreadLocal看当前线程上下文是否有traceContext，
     * 记录入参，捕获到异常则记录异常。
     * @param request 请求
     * @param future 响应的CompletableFuture
     * @param chain 过滤器链
     */
    @Override
    public void doFilter(TrpcRequest request, CompletableFuture<TrpcResponse> future, TrpcClientFilterChain chain) {
        TraceContext currentThreadTraceContext = ZipkinHolder.traceContextThreadLocal.get();
        TrpcZipkinClientRequest requestWrapper = new TrpcZipkinClientRequest(request);
        Span span = handler.handleSendWithParent(requestWrapper, currentThreadTraceContext); // 1.
        Throwable error = null;
        CurrentTraceContext currentTraceContext = this.rpcTracing.tracing().currentTraceContext();
        try (CurrentTraceContext.Scope ws = currentTraceContext.newScope(span.context())) { // 2.
            span.tag("args", FastjsonSerializerUtil.objectToJson(request.getParams()));
            chain.doFilter(request, future);
        } catch (Throwable e) {
            error = e; // 4.
            throw e;
        } finally {
            Throwable finalError = error;
            future.whenComplete(new BiConsumer<TrpcResponse, Throwable>() {
                @Override
                public void accept(TrpcResponse response, Throwable throwable) {
                    span.tag("result", FastjsonSerializerUtil.objectToJson(response.getData()));
                    TrpcZipkinClientResponse responseWrapper = new TrpcZipkinClientResponse(requestWrapper, response, finalError);
                    handler.handleReceive(responseWrapper, span); // 5.
                }
            });
        }

    }
}
