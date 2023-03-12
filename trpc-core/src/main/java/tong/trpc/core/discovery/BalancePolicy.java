package tong.trpc.core.discovery;

import java.util.List;

/**
 * @Author tong-exists
 * @Create 2023/3/5 10:15
 * @Version 1.0
 */
public interface BalancePolicy {

    public int choose(List list);

}
