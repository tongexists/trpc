package tong.provider.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tong.trpc.core.TrpcStarter;
import tong.trpc.core.annotation.TrpcServiceScan;

/**
 * @Author tong-exists
 * @Create 2023/3/15 9:29
 * @Version 1.0
 */
@Slf4j
@SpringBootApplication(scanBasePackages = {"common.api.service", "tong.provider.test", "tong.trpc"})
@TrpcServiceScan(basePackages = "common.api.service")
public class Provider1App {

    public static void main(String[] args) {
        SpringApplication.run(Provider1App.class, args);
        TrpcStarter.run();
    }

}
