package tong.trpc.core;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 前缀名线程工厂
 * @Author tong-exists
 * @Create 2023/3/24 16:06
 * @Version 1.0
 */
public class NamedThreadFactory implements ThreadFactory {

    /**
     * 线程名前缀
     */
    private final String prefix;

    /**
     * 线程编号
     */
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    /**
     * 创建线程工厂
     *
     * @param prefix 线程名前缀
     */
    public NamedThreadFactory(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(null, r, prefix + threadNumber.getAndIncrement());
    }
}