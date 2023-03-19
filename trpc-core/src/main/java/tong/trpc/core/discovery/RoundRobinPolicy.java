package tong.trpc.core.discovery;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  轮询负载均衡算法
 * @Author tong-exists
 * @Create 2023/3/18 16:19
 * @Version 1.0
 */
public class RoundRobinPolicy implements BalancePolicy{

    private ConcurrentHashMap<String, Integer> record = new ConcurrentHashMap<>();
    @Override
    public int choose(String serviceName, List list) {
        Integer idx = record.getOrDefault(serviceName, 0);
        idx += 1;
        if (idx >= list.size()) {
            idx = 0;
        }
        record.put(serviceName, idx);
        return idx;
    }

    @Override
    public String name() {
        return "RoundRobinPolicy";
    }
}
