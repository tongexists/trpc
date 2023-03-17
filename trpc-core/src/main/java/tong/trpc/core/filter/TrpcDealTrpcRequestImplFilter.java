package tong.trpc.core.filter;

import lombok.extern.slf4j.Slf4j;
import tong.trpc.core.domain.*;
import tong.trpc.core.domain.request.TrpcRequest;
import tong.trpc.core.domain.response.TrpcResponse;
import tong.trpc.core.spring.SpringBeanManager;

import java.lang.reflect.Method;

/**
 * @Author tong-exists
 * @Create 2023/3/16 13:29
 * @Version 1.0
 */
@Slf4j
public class TrpcDealTrpcRequestImplFilter implements TrpcServerFilter {

    @Override
    public void doFilter(TrpcRequest request, TrpcResponse response, TrpcServerFilterChain chain) {
        if (request.getRequestType() != TrpcRequestType.TRPC_REQUEST_IMPL.getCode()) {
            chain.doFilter(request, response);
            return;
        }
        try {

            // 反射加载
            String packageName = request.getClassName().substring(
                    0, request.getClassName().lastIndexOf(".")
            );
            String shortClassName = request.getClassName().substring(
                    request.getClassName().lastIndexOf(".") + 1
            );
            if (shortClassName.indexOf(TrpcConstant.TRPC_SERVICE_PRE_CLASS_NAME) != 0) {
                throw new RuntimeException("访问的服务类名未以Trpc开头");
            }
            Class<?> clazz = Class.forName(packageName + "." + shortClassName.substring(4));
            // 加载实例
            Object bean = SpringBeanManager.getBean(clazz);
            // 加载实例调用的方法
            Method method = bean.getClass().getDeclaredMethod(request.getMethodName(), request.getParamsTypes());
            // 通过反射调用

            Object result =  method.invoke(bean, request.getParams());
            response.setResponseType(TrpcResponseType.TRPC_RESPONSE.getCode());
            response.setData(result);
            response.setCode(TrpcResponseCode.SUCCESS.getCode());
            response.setMsg("success");
            response.setRequestId(request.getRequestId());
            response.setReturnType(method.getReturnType());
            chain.doFilter(request, response);
        } catch (Exception e) {
            log.error("", e);
            response.setCode(TrpcResponseCode.ERROR.getCode());
            response.setRequestId(request.getRequestId());
            response.setMsg(e.getMessage());
            chain.doFilter(request, response);
        }

    }


}
