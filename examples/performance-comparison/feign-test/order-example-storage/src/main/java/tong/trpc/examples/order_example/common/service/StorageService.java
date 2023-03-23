package tong.trpc.examples.order_example.common.service;

/**
 * @Author tong-exists
 * @Create 2023/3/18 10:51
 * @Version 1.0
 */
public interface StorageService {

    boolean decreaseStock(Long productId, Integer count);
    boolean decreaseStockDepth(Long productId, Integer count);
    boolean decreaseStockException(Long productId, Integer count);

}
