package tong.trpc.core.filter.client;

import tong.trpc.core.domain.request.TrpcRequest;
import tong.trpc.core.domain.response.TrpcResponse;
import tong.trpc.core.zipkin.TrpcClientTracingInterceptor;
import tong.trpc.core.zipkin.ZipkinHolder;

import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 客户端过滤器静态工具类
 * @Author tong-exists
 * @Create 2023/3/16 13:10
 * @Version 1.0
 */
public class TrpcClientFilters {
    /**
     * 保存过滤器
     */
    private static CopyOnWriteArrayList<TrpcClientFilter> filters = new CopyOnWriteArrayList<>();


    static {
        /*
            添加默认过滤器，[TrpcClientExceptionHandlerFilter]
         */
        add(new TrpcClientExceptionHandlerFilter());
    }

    /**
     * 执行过滤
     * @param request 请求
     * @param response 响应的CompletableFuture
     */
    public static void doFilter(TrpcRequest request, CompletableFuture<TrpcResponse> response) {
        TrpcClientFilterChain chain = new TrpcClientFilterChain(filters.toArray(new TrpcClientFilter[0]));
        chain.doFilter(request, response);
    }

    /**
     * 头插法添加过滤器
     * @param filter 过滤器
     */
    public static void addFirst(TrpcClientFilter filter) {
        filters.add(0, filter);
    }

    /**
     * 尾插法添加过滤器
     * @param filter 过滤器
     */
    public static void addLast(TrpcClientFilter filter) {
        filters.add( filter);
    }

    /**
     * 添加过滤器，按照过滤器的order进行排序
     * @param filter 过滤器
     */
    public static void add(TrpcClientFilter filter) {
        filters.add(filter);
        filters.sort(new Comparator<TrpcClientFilter>() {
            @Override
            public int compare(TrpcClientFilter o1, TrpcClientFilter o2) {
                return o1.order().compareTo(o2.order());
            }
        });
    }
}
