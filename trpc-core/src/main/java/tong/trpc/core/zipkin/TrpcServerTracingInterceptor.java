package tong.trpc.core.zipkin;

import brave.Span;
import brave.Tracer;
import brave.propagation.CurrentTraceContext;
import brave.rpc.RpcServerHandler;
import brave.rpc.RpcTracing;
import tong.trpc.core.domain.TrpcRequest;
import tong.trpc.core.domain.TrpcResponse;
import tong.trpc.core.filter.TrpcServerFilter;
import tong.trpc.core.filter.TrpcServerFilterChain;
import tong.trpc.core.util.FastjsonSerializerUtil;

/**
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
}
