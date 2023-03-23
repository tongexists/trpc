package tong.trpc.examples.order_example.common.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import tong.trpc.examples.order_example.common.domain.Product;
import tong.trpc.examples.order_example.common.service.ProductService;
import tong.trpc.examples.order_example.common.service.StorageService;

/**
 * @Author tong-exists
 * @Create 2023/3/18 10:53
 * @Version 1.0
 */
@DubboService
@Slf4j
public class StorageServiceImpl implements StorageService {
    @Override
    public boolean decreaseStock(Long productId, Integer count) {
        log.info("商品[{}]减少库存{}操作成功", productId, count);
        return true;
    }

    @DubboReference
    private ProductService productService;

    @Override
    public boolean decreaseStockDepth(Long productId, Integer count) {
        log.info("商品[{}]减少库存{}操作成功", productId, count);
        Product sync = productService.getProductById(productId);
        log.info(sync.toString());
        return true;
    }

    @Override
    public boolean decreaseStockException(Long productId, Integer count) {
        log.info("商品[{}]减少库存{}操作失败", productId, count);
        throw new RuntimeException(String.format("商品[%s]减少库存%s操作失败", productId, count));
    }


}
