package tong.trpc.core.zipkin;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.CurrentTraceContext;
import brave.rpc.RpcServerHandler;
import brave.rpc.RpcTracing;
import tong.trpc.core.TrpcConfig;
import tong.trpc.core.domain.request.TrpcRequest;
import tong.trpc.core.domain.response.TrpcResponse;
import tong.trpc.core.filter.server.TrpcServerFilter;
import tong.trpc.core.filter.server.TrpcServerFilterChain;
import tong.trpc.core.filter.server.TrpcServerFiltersOrder;
import tong.trpc.core.util.FastjsonSerializerUtil;
import zipkin2.reporter.brave.AsyncZipkinSpanHandler;
import zipkin2.reporter.urlconnection.URLConnectionSender;

/**
 * 服务端的追踪拦截器
 * @Author tong-exists
 * @Create 2023/3/14 21:17
 * @Version 1.0
 */
public class TrpcServerTracingInterceptor implements TrpcServerFilter {


    private final Tracer tracer;
    private final RpcServerHandler handler;
    private final RpcTracing rpcTracing;


    public TrpcServerTracingInterceptor(RpcTracing rpcTracing) {
        tracer = rpcTracing.tracing().tracer();
        handler = RpcServerHandler.create(rpcTracing);
        this.rpcTracing = rpcTracing;
    }

    /**
     * 设置当前traceContext到线程上下文，以便服务端调用的方法中又调用另一个远程服务的情况也能够串连起来，加入到整体的追踪
     * @param request 请求
     * @param response 响应
     * @param chain 过滤器链
     */
    @Override
    public void doFilter(TrpcRequest request, TrpcResponse response, TrpcServerFilterChain chain) {
        TrpcZipkinServerRequest requestWrapper = new TrpcZipkinServerRequest(request);
        Span span = handler.handleReceive(requestWrapper);
        ZipkinHolder.traceContextThreadLocal.set(span.context());
        Throwable error = null;
        CurrentTraceContext currentTraceContext = this.rpcTracing.tracing().currentTraceContext();
        try (CurrentTraceContext.Scope ws = currentTraceContext.newScope(span.context())) { // 2.
            span.tag("args", FastjsonSerializerUtil.objectToJson(request.getParams()));
            chain.doFilter(request, response);
        } catch (Throwable e) {
            error = e; // 4.
            throw e;
        } finally {
            span.tag("result", FastjsonSerializerUtil.objectToJson(response.getData()));
            TrpcZipkinServerResponse responseWrapper =
                    new TrpcZipkinServerResponse(requestWrapper,response, error);
            handler.handleSend(responseWrapper, span); // 5.
            ZipkinHolder.traceContextThreadLocal.remove();
        }
    }

    @Override
    public boolean isEnable() {
        return TrpcConfig.traceEnable;
    }

    @Override
    public String order() {
        return TrpcServerFiltersOrder.TrpcServerTracingInterceptor.getOrder();
    }
}
