package tong.trpc.core.zipkin;

import brave.Span;
import brave.Tracer;
import brave.propagation.CurrentTraceContext;
import brave.propagation.Propagation;
import brave.propagation.TraceContext;
import brave.rpc.RpcClientHandler;
import brave.rpc.RpcTracing;
import lombok.extern.slf4j.Slf4j;
import tong.trpc.core.TrpcConfig;
import tong.trpc.core.domain.request.TrpcRequest;
import tong.trpc.core.domain.response.TrpcResponse;
import tong.trpc.core.filter.client.TrpcClientFilter;
import tong.trpc.core.filter.client.TrpcClientFilterChain;
import tong.trpc.core.filter.client.TrpcClientFiltersOrder;
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
     * @param chain 过滤器链
     */
    @Override
    public TrpcResponse doFilter(TrpcRequest request, TrpcClientFilterChain chain) {
        TraceContext.Extractor<TrpcRequest> extractor = this.rpcTracing.propagation().extractor(new Propagation.RemoteGetter<TrpcRequest>() {
            @Override
            public Span.Kind spanKind() {
                return Span.Kind.CLIENT;
            }

            @Override
            public String get(TrpcRequest request, String fieldName) {
                return (String) request.getAttributes().get(fieldName);
            }
        });
        TraceContext currentThreadTraceContext = extractor.extract(request).context();
        TrpcZipkinClientRequest requestWrapper = new TrpcZipkinClientRequest(request);
        Span span = handler.handleSendWithParent(requestWrapper, currentThreadTraceContext); // 1.
        Throwable error = null;
        CurrentTraceContext currentTraceContext = this.rpcTracing.tracing().currentTraceContext();
        TrpcResponse response = null;
        try (CurrentTraceContext.Scope ws = currentTraceContext.newScope(span.context())) { // 2.
            span.tag("clientArgs", FastjsonSerializerUtil.objectToJson(request.getParams()));
            response = chain.doFilter(request);
            return response;
        } catch (Throwable e) {
            error = e; // 4.
            throw e;
        } finally {
            Throwable finalError = error;
            span.tag("clientResult", FastjsonSerializerUtil.objectToJson(response == null? "响应为空" : response.getData()));
            TrpcZipkinClientResponse responseWrapper = new TrpcZipkinClientResponse(requestWrapper, response, finalError);
            handler.handleReceive(responseWrapper, span); // 5.
        }

    }

    @Override
    public boolean isEnable() {
        return TrpcConfig.traceEnable;
    }

    @Override
    public String order() {
        return TrpcClientFiltersOrder.TrpcClientTracingInterceptor.getOrder();
    }
}
