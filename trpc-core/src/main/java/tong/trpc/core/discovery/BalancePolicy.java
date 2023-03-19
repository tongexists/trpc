package tong.trpc.core.discovery;

import java.util.List;

/**
 * 负载均衡策略
 * @Author tong-exists
 * @Create 2023/3/5 10:15
 * @Version 1.0
 */
public interface BalancePolicy {

    public int choose(String serviceName, List list);

    String name();

}
