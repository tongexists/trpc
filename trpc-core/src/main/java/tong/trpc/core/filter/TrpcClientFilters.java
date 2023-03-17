package tong.trpc.core.filter;

import tong.trpc.core.domain.request.TrpcRequest;
import tong.trpc.core.domain.response.TrpcResponse;
import tong.trpc.core.zipkin.TrpcClientTracingInterceptor;
import tong.trpc.core.zipkin.ZipkinHolder;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author tong-exists
 * @Create 2023/3/16 13:10
 * @Version 1.0
 */
public class TrpcClientFilters {

    private static CopyOnWriteArrayList<TrpcClientFilter> filters = new CopyOnWriteArrayList<>();

    static {
        addFilter(new TrpcClientTracingInterceptor(ZipkinHolder.rpcTracing));
    }


    public static void doFilter(TrpcRequest request, CompletableFuture<TrpcResponse> response) {
        TrpcClientFilterChain chain = new TrpcClientFilterChain(filters.toArray(new TrpcClientFilter[0]));
        chain.doFilter(request, response);
    }

    public static void addFilter(TrpcClientFilter filter) {
        filters.add(0, filter);
    }

}
