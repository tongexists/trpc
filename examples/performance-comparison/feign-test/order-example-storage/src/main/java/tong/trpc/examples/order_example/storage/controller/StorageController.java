package tong.trpc.examples.order_example.storage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tong.trpc.examples.order_example.common.service.StorageService;

/**
 * @Author tong-exists
 * @Create 2023/3/22 15:50
 * @Version 1.0
 */
@RestController
@RequestMapping("/storage")
public class StorageController {

    @Autowired
    private StorageService storageService;

    @PutMapping("/decreaseStock")
    boolean decreaseStock(@RequestParam("productId") Long productId, @RequestParam("count") Integer count) {
        return storageService.decreaseStock(productId, count);
    }
    @PutMapping("/decreaseStockDepth")
    boolean decreaseStockDepth(@RequestParam("productId") Long productId, @RequestParam("count") Integer count) {
        return storageService.decreaseStockDepth(productId, count);
    }
    @PutMapping("/decreaseStockException")
    boolean decreaseStockException(@RequestParam("productId") Long productId, @RequestParam("count") Integer count) {
        return storageService.decreaseStockException(productId, count);
    }

}
