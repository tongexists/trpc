package tong.trpc.core.filter;

import tong.trpc.core.domain.TrpcRequest;
import tong.trpc.core.domain.TrpcResponse;

import java.util.concurrent.CompletableFuture;

/**
 * @Author tong-exists
 * @Create 2023/3/16 13:12
 * @Version 1.0
 */

public class TrpcClientFilterChain {

    private int index = -1;

    private TrpcClientFilter[] filters;

    public TrpcClientFilterChain(TrpcClientFilter[] filters) {
        this.filters = filters;
    }

    public void doFilter(TrpcRequest request, CompletableFuture<TrpcResponse> response) {
        TrpcClientFilterChain chain = this;
        chain.index += 1;
        if (chain.index >= chain.filters.length) {
            return;
        }
        TrpcClientFilter curFilter = chain.filters[chain.index];
        curFilter.doFilter(request, response, chain);
    }





}
