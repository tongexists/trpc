package tong.trpc.examples.order_example;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



/**
 * @Author tong-exists
 * @Create 2023/3/18 10:16
 * @Version 1.0
 */

@SpringBootApplication
@EnableDubbo
public class ProductApp {

    public static void main(String[] args) {
        SpringApplication.run(ProductApp.class, args);
    }

}
