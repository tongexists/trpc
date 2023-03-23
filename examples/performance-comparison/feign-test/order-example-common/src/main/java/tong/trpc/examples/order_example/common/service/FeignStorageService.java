package tong.trpc.examples.order_example.common.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * @Author tong-exists
 * @Create 2023/3/18 10:51
 * @Version 1.0
 */

@FeignClient("storage")
public interface FeignStorageService {
    @PutMapping("/storage/decreaseStock")
    boolean decreaseStock(@RequestParam("productId") Long productId, @RequestParam("count") Integer count) ;
    @PutMapping("/storage/decreaseStockDepth")
    boolean decreaseStockDepth(@RequestParam("productId") Long productId, @RequestParam("count") Integer count);
    @PutMapping("/storage/decreaseStockException")
    boolean decreaseStockException(@RequestParam("productId") Long productId, @RequestParam("count") Integer count);

}
