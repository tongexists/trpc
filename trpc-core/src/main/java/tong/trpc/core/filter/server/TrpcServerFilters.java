package tong.trpc.core.filter.server;

import brave.Tracing;
import brave.rpc.RpcTracing;
import tong.trpc.core.TrpcConfig;
import tong.trpc.core.domain.request.TrpcRequest;
import tong.trpc.core.domain.response.TrpcResponse;
import tong.trpc.core.filter.client.TrpcClientFilter;
import tong.trpc.core.io.handler.TrpcServerHandler;
import tong.trpc.core.zipkin.TrpcServerTracingInterceptor;
import zipkin2.reporter.brave.AsyncZipkinSpanHandler;
import zipkin2.reporter.urlconnection.URLConnectionSender;

import java.io.IOException;
import java.util.Comparator;
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
        add(new TrpcServerExceptionHandlerFilter());
        add(new TrpcDealTrpcRequestImplFilter());
        add(new TrpcDealTrpcMultipleRequestFilter());
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

    /**
     * 添加过滤器，按照过滤器的order进行排序
     * @param filter 过滤器
     */
    public static void add(TrpcServerFilter filter) {
        filters.add(filter);
        filters.sort(new Comparator<TrpcServerFilter>() {
            @Override
            public int compare(TrpcServerFilter o1, TrpcServerFilter o2) {
                return o1.order().compareTo(o2.order());
            }
        });
    }
}
