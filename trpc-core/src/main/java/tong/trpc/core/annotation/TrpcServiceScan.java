package tong.trpc.core.annotation;

import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;
import tong.trpc.core.spring.TrpcSpringScannerRegister;

import java.lang.annotation.*;

/**
 * 扫描basePackages下的TrpcService接口，代理生成实现类，并注入到spring
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Import(TrpcSpringScannerRegister.class)
@Documented
public @interface TrpcServiceScan {
    String[] basePackages();
}
