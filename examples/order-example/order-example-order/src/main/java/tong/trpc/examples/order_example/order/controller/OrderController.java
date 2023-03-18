package tong.trpc.examples.order_example.order.controller;

import com.esotericsoftware.minlog.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tong.trpc.core.TrpcInvocation;
import tong.trpc.examples.order_example.common.domain.Order;
import tong.trpc.examples.order_example.common.domain.Product;
import tong.trpc.examples.order_example.common.service.TrpcProductService;
import tong.trpc.examples.order_example.common.service.TrpcStorageService;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @Author tong-exists
 * @Create 2023/3/18 10:45
 * @Version 1.0
 */
@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private TrpcStorageService trpcStorageService;

    @Autowired
    private TrpcProductService productService;

    @PostMapping("/createOrder")
    public Order createOrder(@RequestBody Order order) {
        Boolean result = trpcStorageService.decreaseStock(order.getProductId(), order.getCount()).sync();
        Product product = productService.getProduct(order.getProductId()).sync();
        order.setOrderId(new Random(System.currentTimeMillis()).nextLong());
        order.setTotalPrice((long) (product.getPrice() * order.getCount()));
        return order;
    }

    @PostMapping("/createOrderDepth")
    public Order createOrderDepth(@RequestBody Order order) {
        Boolean result = trpcStorageService.decreaseStockDepth(order.getProductId(), order.getCount()).sync();
        Product product = productService.getProduct(order.getProductId()).sync();
        order.setOrderId(new Random(System.currentTimeMillis()).nextLong());
        order.setTotalPrice((long) (product.getPrice() * order.getCount()));
        return order;
    }

    @PostMapping("/createOrderException")
    public Order createOrderException(@RequestBody Order order) {
        Boolean result = trpcStorageService.decreaseStockException(order.getProductId(), order.getCount()).sync();
        Product product = productService.getProduct(order.getProductId()).sync();
        order.setOrderId(new Random(System.currentTimeMillis()).nextLong());
        order.setTotalPrice((long) (product.getPrice() * order.getCount()));
        return order;
    }

    @PostMapping("/createOrderFuture")
    public Order createOrderFuture(@RequestBody Order order) {
        CompletableFuture<Boolean> future = trpcStorageService.decreaseStock(order.getProductId(), order.getCount()).future();
        try {
            Boolean aBoolean = future.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        Product product = productService.getProduct(order.getProductId()).sync();
        order.setOrderId(new Random(System.currentTimeMillis()).nextLong());
        order.setTotalPrice((long) (product.getPrice() * order.getCount()));
        return order;
    }

    @PostMapping("/createOrderCallback")
    public Order createOrderCallback(@RequestBody Order order) {
        trpcStorageService.decreaseStock(order.getProductId(), order.getCount()).callback(new TrpcInvocation.CallBack<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                log.info("减库存:{}", aBoolean.toString());
            }

            @Override
            public void onFailure(Exception e) {
                log.info("减库存失败",e);
            }
        });
        Product product = productService.getProduct(order.getProductId()).sync();
        order.setOrderId(new Random(System.currentTimeMillis()).nextLong());
        order.setTotalPrice((long) (product.getPrice() * order.getCount()));
        return order;
    }

    @PostMapping("/getOrderMultiple")
    public Order[] getOrderMultiple(@RequestBody Long[] orderIds) {
        int len = orderIds.length;
        Object[][] objects = new Object[len][];
        for (int i = 0; i < len; i++) {
            objects[i] = new Object[]{orderIds[i]};
        }
        Product[] sync = productService.getProduct(0L).multipleCall(
                objects
        ).sync();
        Order[] orders = new Order[len];
        for (int i = 0; i < len; i++) {
            Order order = new Order();
            order.setOrderId(orderIds[i]);
            order.setProductId(sync[i].getProductId());
            order.setCount(10);
            order.setTotalPrice((long) (10 * sync[i].getPrice()));
            orders[i] = order;
        }
        return orders;
    }

}
