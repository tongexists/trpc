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
import tong.trpc.core.filter.TrpcClientFilter;
import tong.trpc.core.filter.TrpcClientFilterChain;
import tong.trpc.core.util.FastjsonSerializerUtil;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * @Author tong-exists
 * @Create 2023/3/14 21:17
 * @Version 1.0
 */
@Slf4j
public class TrpcClientTracingInterceptor implements TrpcClientFilter {


    private final Tracer tracer;
    private final RpcClientHandler handler;

    private RpcTracing rpcTracing;

    public TrpcClientTracingInterceptor(RpcTracing rpcTracing) {
        tracer = rpcTracing.tracing().tracer();
        handler = RpcClientHandler.create(rpcTracing);
        this.rpcTracing = rpcTracing;
    }


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
