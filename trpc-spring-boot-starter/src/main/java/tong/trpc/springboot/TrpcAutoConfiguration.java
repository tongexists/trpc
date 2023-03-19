package tong.trpc.springboot;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import tong.trpc.core.TrpcStarter;

import javax.annotation.PostConstruct;

/**
 * Trpc自动装配，扫描tong.trpc包下的bean，注入到spring容器。启动netty server，注册到zookeeper服务发现
 * @Author tong-exists
 * @Create 2023/3/17 14:57
 * @Version 1.0
 */
@Configuration
@ComponentScan(basePackages = {"tong.trpc"})
public class TrpcAutoConfiguration {
    /**
     * 启动netty server，注册到zookeeper服务发现
     */
    @PostConstruct
    public void begin() {
        TrpcStarter.run();
    }
}
