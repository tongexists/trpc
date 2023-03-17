package tong.trpc.core.filter;

import tong.trpc.core.domain.request.TrpcRequest;
import tong.trpc.core.domain.response.TrpcResponse;

/**
 * @Author tong-exists
 * @Create 2023/3/16 13:12
 * @Version 1.0
 */

public class TrpcServerFilterChain {

    private int index = -1;

    private TrpcServerFilter[] filters;

    public TrpcServerFilterChain(TrpcServerFilter[] filters) {
        this.filters = filters;
    }

    public void doFilter(TrpcRequest request, TrpcResponse response) {
        TrpcServerFilterChain chain = this;
        chain.index += 1;
        if (chain.index >= chain.filters.length) {
            return;
        }
        TrpcServerFilter curFilter = chain.filters[chain.index];
        curFilter.doFilter(request, response, chain);
    }





}
