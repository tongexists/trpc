package tong.consumer.test;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tong.trpc.core.annotation.TrpcServiceScan;

@SpringBootApplication(scanBasePackages = {"tong.trpc", "tong.consumer"})
@TrpcServiceScan(basePackages = "common.api.service")
public class ConsumerApp {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApp.class, args);
    }

}