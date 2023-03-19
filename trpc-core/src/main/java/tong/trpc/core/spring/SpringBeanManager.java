package tong.trpc.core.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * bean管理器，间接操作于ApplicationContext
 */
@Component
public class SpringBeanManager implements ApplicationContextAware {
    /**
     * ApplicationContext，用于获取bean
     */
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringBeanManager.applicationContext = applicationContext;
    }

    /**
     * 根据类获取实例
     * @param clazz 类
     * @return 实例
     * @param <T>
     */
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }
}
