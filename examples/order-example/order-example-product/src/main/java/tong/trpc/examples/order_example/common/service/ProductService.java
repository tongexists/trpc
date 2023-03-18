package tong.trpc.examples.order_example.common.service;

import tong.trpc.examples.order_example.common.domain.Product;

/**
 * @Author tong-exists
 * @Create 2023/3/18 11:00
 * @Version 1.0
 */
public interface ProductService {

    Product getProduct(Long productId);

}
