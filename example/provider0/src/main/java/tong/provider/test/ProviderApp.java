package tong.provider.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tong.trpc.core.TrpcStarter;
import tong.trpc.core.annotation.TrpcServiceScan;

@Slf4j
@SpringBootApplication(scanBasePackages = {"common.api.service", "tong.provider.test"})
@TrpcServiceScan(basePackages = "common.api.service")
public class ProviderApp {

    public static void main(String[] args) {
        SpringApplication.run(ProviderApp.class, args);
        TrpcStarter.run( );
    }

}
