package tong.trpc.core;

import lombok.extern.slf4j.Slf4j;
import tong.trpc.core.discovery.TrpcDiscovery;
import tong.trpc.core.io.TrpcServer;

import java.io.IOException;
import java.util.Properties;

/**
 * @Author tong-exists
 * @Create 2023/3/5 16:25
 * @Version 1.0
 */
@Slf4j
public class TrpcStarter {

    public static void run() {
        Properties properties = new Properties();
        try {
            properties.load(TrpcStarter.class.getClassLoader().getResourceAsStream("trpc.properties"));
            String connectZkStr = (String) properties.getOrDefault("connectZkStr", "127.0.0.1:2181");
            String trpcServicesBasePackage = (String) properties.getOrDefault("trpcServicesBasePackage", "common.api.service");
            String serviceName = (String) properties.get("serviceName");
            if (serviceName == null) {
                throw new RuntimeException("未在trpc.properties配置serviceName");
            }

            String serverAddress = (String) properties.getOrDefault("serverAddress", "127.0.0.1");
            String serverPortStr = (String) properties.get("serverPort");
            if (serverPortStr == null) {
                throw new RuntimeException("未在trpc.properties配置serverPort");
            }
            int serverPort = Integer.parseInt(serverPortStr);
            run(connectZkStr, trpcServicesBasePackage, serviceName, serverAddress, serverPort);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void run(String connectZkStr, String trpcServicesBasePackage, String serviceName, String serverAddress, int serverPort) {
        try {

            TrpcDiscovery.initDiscovery(connectZkStr, trpcServicesBasePackage);
            TrpcDiscovery.getDiscovery().start();
            TrpcDiscovery discovery = TrpcDiscovery.getDiscovery();
            if (discovery.addInstance(serviceName, serverAddress, serverPort, "")) {
                log.info("服务注册成功");
            } else {
                log.error("服务注册失败");
                throw new RuntimeException("服务注册失败");
            }
            TrpcServer trpcServer = new TrpcServer(serverAddress,serverPort);
            trpcServer.startNettyServer();
        } catch (Exception e) {
            throw new RuntimeException("trpc启动失败", e);
        }
    }

}
