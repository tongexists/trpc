package tong.trpc.springboot;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import tong.trpc.core.TrpcStarter;

import javax.annotation.PostConstruct;

/**
 * @Author tong-exists
 * @Create 2023/3/17 14:57
 * @Version 1.0
 */
@Configuration
@ComponentScan(basePackages = {"tong.trpc"})
public class TrpcAutoConfiguration {
    @PostConstruct
    public void begin() {
        TrpcStarter.run();
    }
}
