package tong.trpc.core.annotation;

import org.springframework.core.annotation.AliasFor;
import tong.trpc.core.io.serialize.TrpcSerialType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记Trpc远程服务，trpc远程服务接口必须用此注解标记，且需指明服务名
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TrpcService {

    String serviceInstanceName();
    long timeout() default 10000;

    String name() default "";

    TrpcSerialType serialType() default TrpcSerialType.TrpcKryoSerializer;

}
