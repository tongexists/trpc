package tong.provider.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tong.trpc.core.TrpcStarter;
@Slf4j
@SpringBootApplication(scanBasePackages = {"common.api.service", "tong.provider.test", "tong.trpc"})
public class ProviderApp {

    public static void main(String[] args) {
        SpringApplication.run(ProviderApp.class, args);
        TrpcStarter.run("127.0.0.1:2181", "common.api.service",
                "hello-service", "127.0.0.1", 9000
                );
    }

}
