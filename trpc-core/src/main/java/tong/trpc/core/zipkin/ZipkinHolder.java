package tong.trpc.core.zipkin;

import brave.Span;
import brave.Tracing;
import brave.propagation.Propagation;
import brave.propagation.TraceContext;
import brave.rpc.RpcTracing;
import org.checkerframework.checker.units.qual.K;
import tong.trpc.core.TrpcConfig;
import tong.trpc.core.domain.request.TrpcRequest;
import tong.trpc.core.io.handler.TrpcServerHandler;
import zipkin2.reporter.brave.AsyncZipkinSpanHandler;
import zipkin2.reporter.urlconnection.URLConnectionSender;

import java.io.IOException;
import java.util.Properties;

/**
 * 持有线程上下文中的TraceContext，RpcTracing
 * @Author tong-exists
 * @Create 2023/3/16 16:31
 * @Version 1.0
 */
public class ZipkinHolder {
    /**
     * 线程上下文中的TraceContext
     */
    public static ThreadLocal<TraceContext> traceContextThreadLocal = new ThreadLocal<>();
    /**
     * rpc的tracing配置
     */
    public static RpcTracing rpcTracing;





}
