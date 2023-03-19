package tong.trpc.core.zipkin;

import brave.Tracing;
import brave.rpc.RpcTracing;
import tong.trpc.core.TrpcConfig;
import tong.trpc.core.filter.client.TrpcClientFilters;
import tong.trpc.core.filter.server.TrpcServerFilters;
import zipkin2.reporter.brave.AsyncZipkinSpanHandler;
import zipkin2.reporter.urlconnection.URLConnectionSender;

/**
 * zipkin启动器
 * @Author tong-exists
 * @Create 2023/3/19 16:57
 * @Version 1.0
 */
public class ZipkinStarter {

    /**
     *
     * @param serviceName trpc服务端的服务名
     * @param serverAddress trpc服务端的地址
     * @param serverPort trpc服务端的端口
     * @param zipkinUrl zipkin地址
     */
    public static void start(String serviceName, String serverAddress, int serverPort, String zipkinUrl) {
        ZipkinHolder.rpcTracing =  RpcTracing.create(
                Tracing.newBuilder()
                        .localServiceName(serviceName)
                        .localPort(serverPort)
                        .localIp(serverAddress)
                        .addSpanHandler(AsyncZipkinSpanHandler.create(URLConnectionSender.create(zipkinUrl)))
                        .build()
        );
        TrpcClientFilters.add(new TrpcClientTracingInterceptor(ZipkinHolder.rpcTracing));
        TrpcServerFilters.add(new TrpcServerTracingInterceptor(ZipkinHolder.rpcTracing));
    }



}
