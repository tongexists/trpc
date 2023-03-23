package tong.trpc.examples.order_example.order.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tong.trpc.examples.order_example.common.domain.Order;
import tong.trpc.examples.order_example.common.domain.Product;
import tong.trpc.examples.order_example.common.service.ProductService;
import tong.trpc.examples.order_example.common.service.StorageService;

import java.util.Random;

/**
 * @Author tong-exists
 * @Create 2023/3/18 10:45
 * @Version 1.0
 */
@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {

    @DubboReference
    private StorageService trpcStorageService;

    @DubboReference
    private ProductService productService;

    @PostMapping("/testSerializer")
    public void testSerializer(@RequestBody Order order) {
        Product sync = productService.getProductByOrder(order);
        int a = 2;
    }

    /**
     * 创建订单
     * @param order
     * @return
     */
    @PostMapping("/createOrder")
    public Order createOrder(@RequestBody Order order) {
        //减库存
        Boolean result = trpcStorageService.decreaseStock(order.getProductId(), order.getCount());
        //获取商品信息
        Product product = productService.getProductById(order.getProductId());
        order.setOrderId(new Random(System.currentTimeMillis()).nextLong());
        order.setTotalPrice((long) (product.getPrice() * order.getCount()));
        order.setDesc(product.getName());
        return order;
    }

    /**
     * 跟createOrder不一样的地方是，减库存decreaseStockDepth里面会调用一次productService.getProduct，
     * @param order
     * @return
     */
    @PostMapping("/createOrderDepth")
    public Order createOrderDepth(@RequestBody Order order) {
        Boolean result = trpcStorageService.decreaseStockDepth(order.getProductId(), order.getCount());
        Product product = productService.getProductById(order.getProductId());
        order.setOrderId(new Random(System.currentTimeMillis()).nextLong());
        order.setTotalPrice((long) (product.getPrice() * order.getCount()));
        order.setDesc(product.getName());
        return order;
    }

    /**
     * decreaseStockException里面会有异常产生，测试异常情况
     * @param order
     * @return
     */
    @PostMapping("/createOrderException")
    public Order createOrderException(@RequestBody Order order) {
        Boolean result = trpcStorageService.decreaseStockException(order.getProductId(), order.getCount());
        Product product = productService.getProductById(order.getProductId());
        order.setOrderId(new Random(System.currentTimeMillis()).nextLong());
        order.setTotalPrice((long) (product.getPrice() * order.getCount()));
        return order;
    }



}
