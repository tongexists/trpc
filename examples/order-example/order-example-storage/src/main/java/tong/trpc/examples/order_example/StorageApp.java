package tong.trpc.examples.order_example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tong.trpc.core.TrpcStarter;
import tong.trpc.core.annotation.TrpcServiceScan;

/**
 * @Author tong-exists
 * @Create 2023/3/18 10:24
 * @Version 1.0
 */
@SpringBootApplication
@TrpcServiceScan(basePackages = "tong.trpc.examples.order_example.common")
public class StorageApp {

    public static void main(String[] args) {
        SpringApplication.run(StorageApp.class, args);
    }

}