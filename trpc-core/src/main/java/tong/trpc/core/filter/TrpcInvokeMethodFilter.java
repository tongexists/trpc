package tong.trpc.core.filter;

import lombok.extern.slf4j.Slf4j;
import tong.trpc.core.domain.TrpcConstant;
import tong.trpc.core.domain.TrpcRequest;
import tong.trpc.core.domain.TrpcResponse;
import tong.trpc.core.domain.TrpcResponseCode;
import tong.trpc.core.spring.SpringBeanManager;

import java.lang.reflect.Method;

/**
 * @Author tong-exists
 * @Create 2023/3/16 13:29
 * @Version 1.0
 */
@Slf4j
public class TrpcInvokeMethodFilter implements TrpcServerFilter {

    @Override
    public void doFilter(TrpcRequest request, TrpcResponse response, TrpcServerFilterChain chain) {
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
            response.setCode(TrpcResponseCode.SUCCESS.getCode());
            response.setData(result);
            response.setMsg("success");
            response.setRequestId(request.getRequestId());
            response.setReturnType(method.getReturnType().getTypeName());
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
