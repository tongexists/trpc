package tong.trpc.core.filter;

import brave.Tracing;
import brave.rpc.RpcTracing;
import tong.trpc.core.domain.request.TrpcRequest;
import tong.trpc.core.domain.response.TrpcResponse;
import tong.trpc.core.io.handler.TrpcServerHandler;
import tong.trpc.core.zipkin.TrpcServerTracingInterceptor;
import zipkin2.reporter.brave.AsyncZipkinSpanHandler;
import zipkin2.reporter.urlconnection.URLConnectionSender;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author tong-exists
 * @Create 2023/3/16 13:10
 * @Version 1.0
 */
public class TrpcServerFilters {

    private static CopyOnWriteArrayList<TrpcServerFilter> filters = new CopyOnWriteArrayList<>();

    static {
        addFilter(new TrpcDealTrpcRequestImplFilter());
        addFilter(new TrpcDealTrpcMultipleRequestFilter());
        Properties properties = new Properties();
        try {
            properties.load(TrpcServerHandler.class.getClassLoader().getResourceAsStream("trpc.properties"));
            String serviceName = (String) properties.get("serviceName");
            if (serviceName == null) {
                throw new RuntimeException("未在trpc.properties配置serviceName");
            }
            String serverAddress = (String) properties.getOrDefault("serverAddress", "127.0.0.1");
            String serverPortStr = (String) properties.get("serverPort");
            if (serverPortStr == null) {
                throw new RuntimeException("未在trpc.properties配置serverPort");
            }
            int serverPort = Integer.parseInt(serverPortStr);

            TrpcServerTracingInterceptor myServerTracingInterceptor = new TrpcServerTracingInterceptor(RpcTracing.create(
                    Tracing.newBuilder()
                            .localServiceName(serviceName)
                            .localPort(serverPort)
                            .localIp(serverAddress)
                            .addSpanHandler(AsyncZipkinSpanHandler.create(URLConnectionSender.create("http://localhost:9411/api/v2/spans")))
                            .build()
            ));
            addFilter(myServerTracingInterceptor);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void doFilter(TrpcRequest request, TrpcResponse response) {
        TrpcServerFilterChain chain = new TrpcServerFilterChain(filters.toArray(new TrpcServerFilter[0]));
        chain.doFilter(request, response);
    }

    public static void addFilter(TrpcServerFilter filter) {
        filters.add(0, filter);
    }

}
