package tong.trpc.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TrpcTrace注解指示方法有统一的追踪入口和出口。
 * 举个列子
 * method() {
 *     trpcServer1.hello()
 *     trpcServer2.echo()
 * }
 * 1、当这个方法没有被TrpcTrace标记时，zipkin上显示的是两条追踪
 * 2、当这个方法有被TrpcTrace标记时，zipkin上显示的是一条追踪
 * @Author tong-exists
 * @Create 2023/3/16 18:15
 * @Version 1.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TrpcTrace {
}
