package tong.trpc.core.filter;

import tong.trpc.core.domain.TrpcRequest;
import tong.trpc.core.domain.TrpcResponse;

/**
 * @Author tong-exists
 * @Create 2023/3/16 13:04
 * @Version 1.0
 */
public interface TrpcServerFilter {

    void doFilter(TrpcRequest request, TrpcResponse response, TrpcServerFilterChain chain);

}
