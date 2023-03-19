package tong.trpc.core.discovery;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.*;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.curator.x.discovery.details.ServiceCacheListener;
import org.apache.curator.x.discovery.strategies.RoundRobinStrategy;
import tong.trpc.core.TrpcStarter;
import tong.trpc.core.annotation.TrpcService;
import tong.trpc.core.util.SpringUtil;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务发现，基于curator的服务发现框架，采用单例模式
 * @Author tong-exists
 * @Create 2023/3/2 17:51
 * @Version 1.0
 */
@Slf4j
@Getter
public class TrpcDiscovery {
    /**
     * @param connectZkStr zookeeper的连接地址
     * @param trpcServicesBasePackage trpc服务的基包，用于初始化服务缓存，提前从服务中心拉去服务缓存起来
     */
    private TrpcDiscovery(String connectZkStr, String trpcServicesBasePackage) {
        this.connectZkStr = connectZkStr;
        this.trpcServicesBasePackage = trpcServicesBasePackage;
    }

    /**
     * zookeeper的服务存储路径
     */
    private String prefix = "/trpc/discovery/services";
    /**
     * curator的服务发现实例
     */
    private ServiceDiscovery<ServiceInstanceDetails> serviceDiscovery;
    /**
     * curator客户端
     */
    private CuratorFramework client;
    /**
     * 是否连接上zookeeper
     */
    private boolean connected = false;
    /**
     * zookeeper连接地址
     */
    private String connectZkStr;
    /**
     * trpc服务的基包，用于初始化服务缓存，提前从服务中心拉去服务缓存起来
     */
    private String trpcServicesBasePackage;
    /**
     * 服务提供者池，ServiceProvider跟ServiceCache都可以用来获取某个服务名下的实例，ServiceCache效率更高，不会频繁与zookeeper通信，通过监听变化来更新
     */
    private ConcurrentHashMap<String, ServiceProvider<ServiceInstanceDetails>> serviceProviderPool = new ConcurrentHashMap<>();

    /**
     * 服务缓存池，ServiceProvider跟ServiceCache都可以用来获取某个服务名下的实例，ServiceCache效率更高，不会频繁与zookeeper通信，通过监听变化来更新
     */
    private ConcurrentHashMap<String, ServiceCache<ServiceInstanceDetails>> serviceCachePool = new ConcurrentHashMap<>();
    /**
     * 负载均衡算法
     */
    private BalancePolicy balancePolicy;
    /**
     * 单例
     */
    private static TrpcDiscovery instance;
    /**
     * 是否初始化
     */
    private static boolean initialized = false;

    /**
     * @param connectZkStr zookeeper的连接地址
     * @param trpcServicesBasePackage trpc服务的基包，用于初始化服务缓存，提前从服务中心拉去服务缓存起来
     */
    public static void initDiscovery(String connectZkStr, String trpcServicesBasePackage) {
        TrpcDiscovery.initialized = true;
        TrpcDiscovery.instance = new TrpcDiscovery(connectZkStr, trpcServicesBasePackage);
    }

    /**
     * 获取TrpcDiscovery服务发现单例
     * @return 获取TrpcDiscovery服务发现单例
     */
    public static TrpcDiscovery getDiscovery() {
        if (!TrpcDiscovery.initialized) {
            throw  new RuntimeException("请先初始化Discovery");
        }
        return TrpcDiscovery.instance;
    }

    /**
     * 启动服务发现，连接zookeeper，注册服务，拉取服务
     * @return 是否成功
     */
    public boolean start() {
        if (connected) {
            return true;
        }
        // 连接zookeeper
        client = CuratorFrameworkFactory.newClient(connectZkStr, 1000 * 60 * 60, 1000 * 60 * 60, new RetryNTimes(10, 1000));
        client.start();
        // 启动curator服务发现
        JsonInstanceSerializer<ServiceInstanceDetails> serializer = new JsonInstanceSerializer<ServiceInstanceDetails>(ServiceInstanceDetails.class);
        serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceInstanceDetails.class).client(client).basePath(prefix).serializer(serializer).build();
        try {
            serviceDiscovery.start();
            //拉取服务
            initTrpcServiceCaches();
            // 初始化负载均衡算法
            initBalancePolicy();
            this.connected = true;
            return true;
        } catch (Exception e) {
            log.error("连接zookeeper失败, 或初始化ServiceDiscovery失败", e);
            return false;
        }
    }

    /**
     * 注册服务到服务中心
     * @param serviceName 服务名
     * @param address ip地址
     * @param port 端口
     * @param description 描述
     * @return 是否成功
     */
    public boolean addInstance(String serviceName, String address, int port, String description) {
        try {
            ServiceInstance<ServiceInstanceDetails> serviceInstance = null;
            serviceInstance = ServiceInstance.<ServiceInstanceDetails>builder()
                    .name(serviceName)
                    .address(address)
                    .payload(new ServiceInstanceDetails(description))
                    .port(port)
                    .build();
            serviceDiscovery.registerService(serviceInstance);
            return true;
        } catch (Exception e) {
            log.error(String.format("添加服务实例错误，服务名为%s, ip为%s, 端口为%s", serviceName, address, port), e);
            return false;
        }
    }

    /**
     * 初始化ServiceProvider
     * @param serviceName 服务名
     */
    public void initServiceProvider(String serviceName) {
        ServiceProvider<ServiceInstanceDetails> provider = serviceProviderPool.get(serviceName);
        if (provider == null) {
            provider = serviceDiscovery.serviceProviderBuilder().
                    serviceName(serviceName).providerStrategy(new RoundRobinStrategy<>()).build();
            try {
                provider.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            serviceProviderPool.put(serviceName, provider);
        }
    }

    /**
     * 初始化多个ServiceProvider
     * @param serviceNames 服务名集合
     */
    public void initServiceProviders(Collection<String> serviceNames) {
        for (String serviceName : serviceNames) {
            initServiceProvider(serviceName);
        }
    }

    /**
     * 初始化服务缓存
     * @param serviceNames 服务名集合
     * @return 是否成功
     */
    public boolean initServiceCaches(Collection<String> serviceNames) {
        for (String serviceName : serviceNames) {
            if (!initServiceCache(serviceName)) {
                log.error("添加服务缓存失败");
            }
        }
        return true;
    }

    /**
     * 初始化服务缓存
     * @param serviceName 服务名
     * @return 是否成功
     */
    public boolean initServiceCache(String serviceName) {
        if (serviceCachePool.get(serviceName) != null) {
            return true;
        }
        ServiceCache<ServiceInstanceDetails> serviceCache = serviceDiscovery.serviceCacheBuilder().name(serviceName).build();
        try {
            serviceCache.startImmediate();
            serviceCachePool.put(serviceName, serviceCache);
            return true;
        } catch (Exception e) {
            log.error("添加服务缓存失败",e);
            return false;
        }
    }

    /**
     * 初始化trpc服务缓存，找到被TrpcService注解的类，获取服务名，从注册中心拉取服务
     */
    public void initTrpcServiceCaches() {
        try {
            List<Annotation> annotations = SpringUtil.findAnnotations(trpcServicesBasePackage, "/**/*.class", TrpcService.class);
            for (Annotation annotation : annotations) {
                TrpcService trpcService = (TrpcService)annotation;
                initServiceCache(trpcService.serviceInstanceName());
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 添加服务缓存
     * @param serviceName 服务名
     * @return 是否成功
     */
    public boolean addServiceCache(String serviceName) {
        if (initServiceCache(serviceName)) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * 初始化负载均衡算法
     */
    private void initBalancePolicy() {
        Properties properties = new Properties();
        try {
            properties.load(TrpcStarter.class.getClassLoader().getResourceAsStream("trpc.properties"));
            String balancePolicy = (String) properties.get("balancePolicyClassName");
            if (balancePolicy == null) {
                balancePolicy = "tong.trpc.core.discovery.RandomPolicy";
            }
            Class<?> clazz = Class.forName(balancePolicy);
            this.balancePolicy = (BalancePolicy) clazz.newInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取服务实例的地址
     * @param serviceName 服务名
     * @return ip:port
     */
    public String getInstance(String serviceName) {
        ServiceCache<ServiceInstanceDetails> serviceCache = serviceCachePool.get(serviceName);
        // 没有缓存服务，先添加
        if (serviceCache == null) {
            if (!addServiceCache(serviceName)) {
                log.error("添加服务缓存失败");
                throw new RuntimeException("添加服务缓存失败");
            }
            serviceCache = serviceCachePool.get(serviceName);
        }
        // 拿到服务名下的列表
        List<ServiceInstance<ServiceInstanceDetails>> instances = serviceCache.getInstances();
        // 用负载均衡算法选择一个
        int idx = this.balancePolicy.choose(serviceName, instances);
        ServiceInstance<ServiceInstanceDetails> instance = instances.get(idx);
        return instance.getAddress() + ":" + instance.getPort();
    }

    /**
     *  获取最新的一个服务，使用的是轮询负载均衡算法
     * @param serviceName 服务吗
     * @return ip:port
     */
    public String getInstanceLatest(String serviceName) {
        try {
            ServiceProvider<ServiceInstanceDetails> provider = serviceProviderPool.get(serviceName);
            if (provider == null) {
                provider = serviceDiscovery.serviceProviderBuilder().
                        serviceName(serviceName).providerStrategy(new RoundRobinStrategy<>()).build();
                provider.start();
                Thread.sleep(2500);
                serviceProviderPool.put(serviceName, provider);
            }

            ServiceInstance<ServiceInstanceDetails> instance = provider.getInstance();
            return instance.getAddress() + ":" + instance.getPort();
        } catch (Exception e) {
            log.error("获取服务实例错误", e);
            return null;
        }
    }




}
