package tong.trpc.examples.order_example.common.service;

import tong.trpc.core.invocation.TrpcInvocation;
import tong.trpc.core.annotation.TrpcService;
import tong.trpc.examples.order_example.common.domain.Product;

/**
 * @Author tong-exists
 * @Create 2023/3/18 11:00
 * @Version 1.0
 */
@TrpcService(serviceInstanceName = "product")
public interface TrpcProductService {

    TrpcInvocation<Product> getProduct(Long productId);

}
