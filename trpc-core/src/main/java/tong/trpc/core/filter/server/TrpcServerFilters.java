package tong.trpc.core.filter.server;

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
 * 服务端过滤器静态工具类
 * @Author tong-exists
 * @Create 2023/3/16 13:10
 * @Version 1.0
 */
public class TrpcServerFilters {
    /**
     * 保存过滤器
     */
    private static CopyOnWriteArrayList<TrpcServerFilter> filters = new CopyOnWriteArrayList<>();

    static {
          /*
            添加默认过滤器，[TrpcServerExceptionHandlerFilter, TrpcServerTracingInterceptor
            ，TrpcDealTrpcRequestImplFilter， TrpcDealTrpcMultipleRequestFilter]
         */
        addLast(new TrpcServerExceptionHandlerFilter());
        addServerTracingInterceptor();
        addLast(new TrpcDealTrpcRequestImplFilter());
        addLast(new TrpcDealTrpcMultipleRequestFilter());
    }

    /**
     * 添加TrpcServerTracingInterceptor，需要从trpc.properties读取配置
     */
    private static void addServerTracingInterceptor() {
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
            addLast(myServerTracingInterceptor);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 执行过滤
     * @param request 请求
     * @param response 响应的CompletableFuture
     */
    public static void doFilter(TrpcRequest request, TrpcResponse response) {
        TrpcServerFilterChain chain = new TrpcServerFilterChain(filters.toArray(new TrpcServerFilter[0]));
        chain.doFilter(request, response);
    }
    /**
     * 头插法添加过滤器
     * @param filter 过滤器
     */
    public static void addFirst(TrpcServerFilter filter) {
        filters.add(0, filter);
    }
    /**
     * 尾插法添加过滤器
     * @param filter 过滤器
     */
    public static void addLast(TrpcServerFilter filter) {
        filters.add(filter);
    }

}
