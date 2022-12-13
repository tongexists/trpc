package tong.trpc.core;

import tong.trpc.core.annotation.TrpcService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class TrpcServiceProxyInvocationHandler implements InvocationHandler {

    private final Class<?> clazz;

    public TrpcServiceProxyInvocationHandler(Class<?> clazz) {
        this.clazz = clazz;
    }

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
