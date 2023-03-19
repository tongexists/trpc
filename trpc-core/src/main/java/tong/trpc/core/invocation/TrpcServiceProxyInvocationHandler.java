package tong.trpc.core.invocation;

import tong.trpc.core.annotation.TrpcService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 代理TrpcService接口的调用处理器
 */
public class TrpcServiceProxyInvocationHandler implements InvocationHandler {
    /**
     * TrpcService接口的class对象
     */
    private final Class<?> clazz;

    public TrpcServiceProxyInvocationHandler(Class<?> clazz) {
        this.clazz = clazz;
    }

    /*
    * 获取TrpcService注解的数据，设置到TrpcInvocation
    * */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        TrpcService trpcService = clazz.getAnnotation(TrpcService.class);
        TrpcInvocation trpcInvocation = new TrpcInvocation();
        trpcInvocation.setServiceInstanceName(trpcService.serviceInstanceName());
        trpcInvocation.setTimeout(trpcService.timeout());
        trpcInvocation.setSerialType(trpcService.serialType());
        trpcInvocation.setServiceInterfaceName(clazz.getName());
        trpcInvocation.setMethodName(method.getName());
        trpcInvocation.setParams(args);
        trpcInvocation.setParamsTypes(method.getParameterTypes());

        return trpcInvocation;
    }

}
