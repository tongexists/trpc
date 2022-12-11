package tong.trpc.core.annotation;

import org.springframework.context.annotation.Import;
import tong.trpc.core.spring.TrpcSpringScannerRegister;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Import(TrpcSpringScannerRegister.class)
@Documented
public @interface TrpcServiceScan {
    String[] basePackages() default {};
}
