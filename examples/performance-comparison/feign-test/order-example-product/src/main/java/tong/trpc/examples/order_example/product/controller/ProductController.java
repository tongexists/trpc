package tong.trpc.examples.order_example.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tong.trpc.examples.order_example.common.domain.Order;
import tong.trpc.examples.order_example.common.domain.Product;
import tong.trpc.examples.order_example.common.service.ProductService;

/**
 * @Author tong-exists
 * @Create 2023/3/22 15:45
 * @Version 1.0
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/getProductById")
    Product getProductById(@RequestParam("productId") Long productId) {
        return productService.getProduct(productId);
    }

    @PostMapping("/getProductByOrder")
    Product getProductByOrder(@RequestBody Order order) {
        return productService.getProduct(order);
    }

}
