package tong.trpc.core;

import lombok.extern.slf4j.Slf4j;
import tong.trpc.core.discovery.TrpcDiscovery;
import tong.trpc.core.io.TrpcServer;

import java.io.IOException;
import java.util.Properties;

/**
 * trpc启动器，启动netty server，注册服务到zookeeper
 * @Author tong-exists
 * @Create 2023/3/5 16:25
 * @Version 1.0
 */
@Slf4j
public class TrpcStarter {

    /**
     * 读取trpc.properties的配置，启动netty server，注册服务到zookeeper
     */
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

    /**
     * 启动netty server，注册服务到zookeeper
     * @param connectZkStr zookeeper的连接地址
     * @param trpcServicesBasePackage trpc服务的基包，用于初始化服务缓存，提前从服务中心拉去服务缓存起来
     * @param serviceName trpc服务端的服务名
     * @param serverAddress trpc服务端的地址
     * @param serverPort trpc服务端的端口
     */
    public static void run(String connectZkStr, String trpcServicesBasePackage, String serviceName, String serverAddress, int serverPort) {
        try {
            // 启动服务发现，注册服务，发现服务
            TrpcDiscovery.initDiscovery(connectZkStr, trpcServicesBasePackage);
            TrpcDiscovery.getDiscovery().start();
            TrpcDiscovery discovery = TrpcDiscovery.getDiscovery();
            if (discovery.addInstance(serviceName, serverAddress, serverPort, "")) {
                log.info("服务注册成功");
            } else {
                log.error("服务注册失败");
                throw new RuntimeException("服务注册失败");
            }
            // 启动netty server
            TrpcServer trpcServer = new TrpcServer(serverAddress,serverPort);
            trpcServer.startNettyServer();
        } catch (Exception e) {
            throw new RuntimeException("trpc启动失败", e);
        }
    }

}
