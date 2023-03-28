package tong.trpc.examples.order_example.common.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tong.trpc.examples.order_example.common.domain.Product;
import tong.trpc.examples.order_example.common.service.StorageService;
import tong.trpc.examples.order_example.common.service.TrpcProductService;

/**
 * @Author tong-exists
 * @Create 2023/3/18 10:53
 * @Version 1.0
 */
@Service
@Slf4j
public class StorageServiceImpl implements StorageService {
    @Override
    public Integer decreaseStock(Long productId, Integer count) {
        if (productId <= 0 || productId >= 100) {
            throw new RuntimeException("商品id无效");
        }
        if (count <= 0 || count >= 200) {
            throw new RuntimeException("商品数量无效");
        }
        log.info("商品[{}]减少库存{}操作成功", productId, count);
        return count;
    }

    @Autowired
    private TrpcProductService productService;

    @Override
    public Integer decreaseStockDepth(Long productId, Integer count) {
        if (productId <= 0 || productId >= 100) {
            throw new RuntimeException("商品id无效");
        }
        if (count <= 0 || count >= 200) {
            throw new RuntimeException("商品数量无效");
        }

        log.info("商品[{}]减少库存{}操作成功", productId, count);
        Product sync = productService.getProduct(productId).sync();
        log.info(sync.toString());
        return count;
    }

    @Override
    public Integer decreaseStockException(Long productId, Integer count) {
        if (productId <= 0 || productId >= 100) {
            throw new RuntimeException("商品id无效");
        }
        if (count <= 0 || count >= 200) {
            throw new RuntimeException("商品数量无效");
        }

        log.info("商品[{}]减少库存{}操作失败", productId, count);
        throw new RuntimeException(String.format("商品[%s]减少库存%s操作失败", productId, count));
    }


}
