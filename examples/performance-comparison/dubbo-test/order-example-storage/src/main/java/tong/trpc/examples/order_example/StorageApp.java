package tong.trpc.examples.order_example;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * @Author tong-exists
 * @Create 2023/3/18 10:24
 * @Version 1.0
 */
@SpringBootApplication
@EnableDubbo
public class StorageApp {

    public static void main(String[] args) {
        SpringApplication.run(StorageApp.class, args);
    }

}