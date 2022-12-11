package tong.trpc.core.annotation;

import tong.trpc.core.io.serialize.TrpcSerialType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TrpcService {

    String serviceInstanceName();

    long timeout() default 10000;

    String name() default "";

    TrpcSerialType serialType() default TrpcSerialType.TrpcJdkSerializer;

}
