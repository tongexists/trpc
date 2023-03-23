package tong.trpc.examples.order_example.common.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import tong.trpc.examples.order_example.common.domain.Order;
import tong.trpc.examples.order_example.common.domain.Product;
import tong.trpc.examples.order_example.common.service.ProductService;

/**
 * @Author tong-exists
 * @Create 2023/3/18 11:02
 * @Version 1.0
 */
@DubboService
@Slf4j
public class ProductServiceImpl implements ProductService {
    @Override
    public Product getProductById(Long productId) {
        Product product = new Product();
        product.setProductId(productId);
        product.setName(String.format("Name[%s]", product.getProductId()));
        product.setDesc(String.format("Desc[%s]", product.getProductId()));
        product.setPrice(10);
        log.info("查询到{}", product.toString());
        return product;
    }

    @Override
    public Product getProductByOrder(Order order) {
        Product product = new Product();
        product.setProductId(order.getProductId());
        product.setName(String.format("Name[%s]", product.getProductId()));
        product.setDesc(order.getDesc());
        product.setPrice(10);
        return product;
    }
}
