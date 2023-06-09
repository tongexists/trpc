package tong.trpc.core;

import io.netty.util.NettyRuntime;
import tong.trpc.core.zipkin.ZipkinHolder;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Trpc的配置
 * @Author tong-exists
 * @Create 2023/3/19 15:22
 * @Version 1.0
 */
public class TrpcConfig {

    public static class Constant {
        public static final String traceEnable = "traceEnable";
    }

    /**
     * zookeeper的连接地址
     */
    public static String connectZkStr;
    /**
     * trpc服务的基包，用于初始化服务缓存，提前从服务中心拉去服务缓存起来
     */
    public static String trpcServicesBasePackage;
    /**
     * trpc服务端的服务名
     */
    public static String serviceName;
    /**
     * trpc服务端的地址
     */
    public static String serverAddress;
    /**
     * trpc服务端的端口
     */
    public static int serverPort;
    /**
     * 是否开启分布式追踪
     */
    public static boolean traceEnable;
    /**
     * zipkin的地址
     */
    public static String zipkinUrl;
    /**
     * 负载均衡策略
     */
    public static String balancePolicyClassName;
    /**
     * 服务端工作线程数
     */
    public static int serverWorkThreads;
    /**
     * 服务端处理连接请求的线程数
     */
    public static int serverAcceptRequestThreads;
    /**
     * 客户工作线程数
     */
    public static int clientWorkThreads;

    /**
     * 客户端写操作空闲时间阈值，超过这个时间客户端会关闭
     */
    public static long clientWriteIdleThreshold;

    /**
     * 客户端过滤器处理的线程池最小数量
     */
    public static int clientFiltersThreadPoolMinSize;
    /**
     * 客户端过滤器处理的线程池最大数量
     */
    public static int clientFiltersThreadPoolMaxSize;
    /**
     * 客户端过滤器处理的线程池缓冲队列大小
     */
    public static int clientFiltersThreadPoolQueueSize;

    /**
     * 客户端过滤器处理的线程池
     */
    private static ThreadPoolExecutor clientFiltersThreadPool;

    public static ThreadPoolExecutor getClientFiltersThreadPool() {
        return clientFiltersThreadPool;
    }

    /**
     * 从trpc.properties中加载配置
     */
    public static void loadFromTrpcProperties() {
        Properties properties = new Properties();
        try {
            properties.load(TrpcStarter.class.getClassLoader().getResourceAsStream("trpc.properties"));
            connectZkStr = (String) properties.getOrDefault("connectZkStr", "127.0.0.1:2181");
            trpcServicesBasePackage = (String) properties.get("trpcServicesBasePackage");
            if (trpcServicesBasePackage == null) {
                throw new RuntimeException("未在trpc.properties配置trpcServicesBasePackage");
            }
            serviceName = (String) properties.get("serviceName");
            if (serviceName == null) {
                throw new RuntimeException("未在trpc.properties配置serviceName");
            }

            serverAddress = (String) properties.getOrDefault("serverAddress", "127.0.0.1");
            String serverPortStr = (String) properties.get("serverPort");
            if (serverPortStr == null) {
                throw new RuntimeException("未在trpc.properties配置serverPort");
            }
            serverPort = Integer.parseInt(serverPortStr);
            balancePolicyClassName = (String) properties.getOrDefault("balancePolicyClassName", "tong.trpc.core.discovery.RoundRobinPolicy");

            //分布式追踪
            traceEnable = Boolean.parseBoolean((String) properties.getOrDefault("traceEnable", "false"));
            if (traceEnable) {
                zipkinUrl = (String) properties.get("zipkinUrl");
                if (zipkinUrl == null) {
                    throw new RuntimeException("未在trpc.properties配置zipkinUrl");
                }
            }

            String defaultThreads = "" + NettyRuntime.availableProcessors() * 2;
            serverWorkThreads = Integer.parseInt((String) properties.getOrDefault("serverWorkThreads", defaultThreads));
            serverAcceptRequestThreads = Integer.parseInt((String) properties.getOrDefault("serverAcceptRequestThreads", defaultThreads));
            clientWorkThreads = Integer.parseInt((String) properties.getOrDefault("clientWorkThreads", defaultThreads));

            clientWriteIdleThreshold = Long.parseLong((String) properties.getOrDefault("clientWriteIdleThreshold", "60000"));


            clientFiltersThreadPoolMinSize = Integer.parseInt((String) properties.getOrDefault("clientFiltersThreadPoolMinSize", "10"));
            clientFiltersThreadPoolMaxSize = Integer.parseInt((String) properties.getOrDefault("clientFiltersThreadPoolMaxSize", "20"));
            clientFiltersThreadPoolQueueSize = Integer.parseInt((String) properties.getOrDefault("clientFiltersThreadPoolQueueSize", "1000"));
            clientFiltersThreadPool = new ThreadPoolExecutor(
                    clientFiltersThreadPoolMinSize,
                    clientFiltersThreadPoolMaxSize,
                    60L, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(clientFiltersThreadPoolQueueSize),
                    new NamedThreadFactory("trpc-client-filters-"),
                    new ThreadPoolExecutor.AbortPolicy());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从trpc.properties中获取配置
     * @param key
     * @return
     */
    public static String getProperty(String key) {
        Properties properties = new Properties();
        try {
            properties.load(TrpcStarter.class.getClassLoader().getResourceAsStream("trpc.properties"));
            return properties.getProperty(key);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
