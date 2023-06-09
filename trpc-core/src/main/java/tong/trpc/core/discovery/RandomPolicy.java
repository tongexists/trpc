package tong.trpc.core.discovery;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * 随机负载均衡策略
 * @Author tong-exists
 * @Create 2023/3/5 10:16
 * @Version 1.0
 */
public class RandomPolicy implements BalancePolicy {

    private ThreadLocal<Random> threadLocalRandom = ThreadLocal.withInitial(new Supplier<Random>() {
        @Override
        public Random get() {
            return new Random(System.currentTimeMillis());
        }
    });

    @Override
    public int choose(String serviceName, List list) {
        return threadLocalRandom.get().nextInt(list.size());
    }

    @Override
    public String name() {
        return "RandomPolicy";
    }


}
