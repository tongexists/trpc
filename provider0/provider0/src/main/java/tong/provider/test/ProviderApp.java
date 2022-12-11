package tong.provider.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tong.trpc.core.io.server.TrpcServer;

@SpringBootApplication(scanBasePackages = {"common.api.service", "tong.provider.test", "tong.trpc"})
public class ProviderApp {

    public static void main(String[] args) {
        SpringApplication.run(ProviderApp.class, args);
        TrpcServer trpcServer = new TrpcServer("127.0.0.1",9000);
        trpcServer.startNettyServer();
    }

}
