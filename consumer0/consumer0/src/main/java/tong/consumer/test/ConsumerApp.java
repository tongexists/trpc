package tong.consumer.test;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tong.trpc.core.TrpcStarter;
import tong.trpc.core.annotation.TrpcServiceScan;

@SpringBootApplication(scanBasePackages = {"tong.trpc", "tong.consumer"})
@TrpcServiceScan(basePackages = "common.api.service")
@Slf4j
public class ConsumerApp {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApp.class, args);
        TrpcStarter.run("127.0.0.1:2181", "common.api.service",
                "hello-service-consumer", "127.0.0.1", 9001
        );
    }

}