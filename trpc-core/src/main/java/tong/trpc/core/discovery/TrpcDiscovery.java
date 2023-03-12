package tong.trpc.core.discovery;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.*;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.curator.x.discovery.strategies.RoundRobinStrategy;
import tong.trpc.core.annotation.TrpcService;
import tong.trpc.core.util.SpringUtil;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author tong-exists
 * @Create 2023/3/2 17:51
 * @Version 1.0
 */
@Slf4j
@Getter
public class TrpcDiscovery {

    private TrpcDiscovery(String connectZkStr, String trpcServicesBasePackage) {
        this.connectZkStr = connectZkStr;
        this.trpcServicesBasePackage = trpcServicesBasePackage;
    }

    private String prefix = "/trpc/discovery/services";
    private ServiceDiscovery<ServiceInstanceDetails> serviceDiscovery;
    private CuratorFramework client;
    private boolean connected = false;
    private String connectZkStr;
    private String trpcServicesBasePackage;
    private ConcurrentHashMap<String, ServiceProvider<ServiceInstanceDetails>> serviceProviderPool = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, ServiceCache<ServiceInstanceDetails>> serviceCachePool = new ConcurrentHashMap<>();

    private static TrpcDiscovery instance;

    private static boolean initialized = false;

    public static void initDiscovery(String connectZkStr, String trpcServicesBasePackage) {
        TrpcDiscovery.initialized = true;
        TrpcDiscovery.instance = new TrpcDiscovery(connectZkStr, trpcServicesBasePackage);
    }

    public static TrpcDiscovery getDiscovery() {
        if (!TrpcDiscovery.initialized) {
            throw  new RuntimeException("请先初始化Discovery");
        }
        return TrpcDiscovery.instance;
    }

    public boolean start() {
        if (connected) {
            return true;
        }
        client = CuratorFrameworkFactory.newClient(connectZkStr, 1000 * 60 * 60, 1000 * 60 * 60, new RetryNTimes(10, 1000));
        client.start();

        JsonInstanceSerializer<ServiceInstanceDetails> serializer = new JsonInstanceSerializer<ServiceInstanceDetails>(ServiceInstanceDetails.class);
        serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceInstanceDetails.class).client(client).basePath(prefix).serializer(serializer).build();
        try {
            serviceDiscovery.start();
            registerAllTrpcService();
            this.connected = true;
            return true;
        } catch (Exception e) {
            log.error("连接zookeeper失败, 或初始化ServiceDiscovery失败", e);
            return false;
        }
    }

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

    public void initServiceProviders(Collection<String> serviceNames) {
        for (String serviceName : serviceNames) {
            initServiceProvider(serviceName);
        }
    }

    public boolean initServiceCaches(Collection<String> serviceNames) {
        for (String serviceName : serviceNames) {
            if (!initServiceCache(serviceName)) {
                log.error("添加服务缓存失败");
            }
        }
        return true;
    }

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

    public void registerAllTrpcService() {
        try {
            List<Annotation> annotations = SpringUtil.findAnnotations(trpcServicesBasePackage, "/**/*.class", TrpcService.class);
            for (Annotation annotation : annotations) {
                TrpcService trpcService = (TrpcService)annotation;
                addServiceCache(trpcService.serviceInstanceName());
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

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

    public String getInstance(String serviceName) {
        ServiceCache<ServiceInstanceDetails> serviceCache = serviceCachePool.get(serviceName);
        if (serviceCache == null) {
            if (!addServiceCache(serviceName)) {
                log.error("添加服务缓存失败");
                throw new RuntimeException("添加服务缓存失败");
            }
            serviceCache = serviceCachePool.get(serviceName);
        }
        List<ServiceInstance<ServiceInstanceDetails>> instances = serviceCache.getInstances();
        int idx = RandomPolicy.randomPolicy.choose(instances);
        ServiceInstance<ServiceInstanceDetails> instance = instances.get(idx);
        return instance.getAddress() + ":" + instance.getPort();
    }

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
