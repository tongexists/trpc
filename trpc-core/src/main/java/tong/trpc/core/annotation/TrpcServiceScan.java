package tong.trpc.core.annotation;

import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;
import tong.trpc.core.spring.TrpcSpringScannerRegister;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Import(TrpcSpringScannerRegister.class)
@Documented
public @interface TrpcServiceScan {
    String[] basePackages() default {};
    @AliasFor("basePackages")
    String[] value() default {};
}
