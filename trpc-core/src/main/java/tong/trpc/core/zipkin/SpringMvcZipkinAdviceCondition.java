package tong.trpc.core.zipkin;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import tong.trpc.core.TrpcConfig;

/**
 * SpringMvcZipkinAdvice的注入spring的条件
 * @Author tong-exists
 * @Create 2023/3/19 16:06
 * @Version 1.0
 */
public class SpringMvcZipkinAdviceCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return Boolean.parseBoolean(TrpcConfig.getProperty(TrpcConfig.Constant.traceEnable));
    }
}
