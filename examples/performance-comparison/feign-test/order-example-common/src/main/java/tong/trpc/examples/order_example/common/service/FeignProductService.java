package tong.trpc.examples.order_example.common.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import tong.trpc.examples.order_example.common.domain.Order;
import tong.trpc.examples.order_example.common.domain.Product;

/**
 * @Author tong-exists
 * @Create 2023/3/18 11:00
 * @Version 1.0
 */

@FeignClient("product")
public interface FeignProductService {


    @GetMapping("/product/getProductById")
    Product getProductById(@RequestParam("productId") Long productId) ;


    @PostMapping("/product/getProductByOrder")
    Product getProductByOrder(@RequestBody Order order) ;

}
