package tong.trpc.examples.order_example.common.service;

import tong.trpc.core.TrpcInvocation;
import tong.trpc.core.annotation.TrpcService;

/**
 * @Author tong-exists
 * @Create 2023/3/18 10:51
 * @Version 1.0
 */
@TrpcService(serviceInstanceName = "storage")
public interface TrpcStorageService {

    TrpcInvocation<Boolean> decreaseStock(Long productId, Integer count);
    TrpcInvocation<Boolean> decreaseStockDepth(Long productId, Integer count);
    TrpcInvocation<Boolean> decreaseStockException(Long productId, Integer count);
}
