package tong.trpc.core.filter.server;

import lombok.extern.slf4j.Slf4j;
import tong.trpc.core.domain.*;
import tong.trpc.core.domain.request.TrpcMultipleRequest;
import tong.trpc.core.domain.request.TrpcRequest;
import tong.trpc.core.domain.response.TrpcMultipleResponse;
import tong.trpc.core.domain.response.TrpcResponse;
import tong.trpc.core.spring.SpringBeanManager;

import java.lang.reflect.Method;

/**
 * TrpcMultipleRequest的处理器
 * @Author tong-exists
 * @Create 2023/3/16 13:29
 * @Version 1.0
 */
@Slf4j
public class TrpcDealTrpcMultipleRequestFilter implements TrpcServerFilter {
    /**
     * 若不是TrpcMultipleRequest，则不处理，跳过。
     * 从TrpcMultipleRequest中提取反射获取方法需要的类名、方法名、入参类型，遍历实参数组反射调用方法，
     * 将多个执行结果存到结果数组，放进TrpcMultipleResponse。
     * @param requestRaw
     * @param responseRaw
     * @param chain
     */
    @Override
    public void doFilter(TrpcRequest requestRaw, TrpcResponse responseRaw, TrpcServerFilterChain chain) {
        if (!(requestRaw instanceof TrpcMultipleRequest)) {
            chain.doFilter(requestRaw, responseRaw);
            return;
        }
        TrpcMultipleRequest request = (TrpcMultipleRequest)requestRaw;
        TrpcMultipleResponse response = (TrpcMultipleResponse)responseRaw;

        try {
            String packageName = request.getClassName().substring(
                    0, request.getClassName().lastIndexOf(".")
            );
            String shortClassName = request.getClassName().substring(
                    request.getClassName().lastIndexOf(".") + 1
            );
            // 传过来的类名需要以Trpc开头，实际需要的是去除Trpc的类名
            if (shortClassName.indexOf(TrpcConstant.TRPC_SERVICE_PRE_CLASS_NAME) != 0) {
                throw new RuntimeException("访问的服务类名未以Trpc开头");
            }
            Class<?> clazz = Class.forName(packageName + "." + shortClassName.substring(4));
            // 加载实例
            Object bean = SpringBeanManager.getBean(clazz);
            // 加载实例调用的方法
            Method method = bean.getClass().getDeclaredMethod(request.getMethodName(), request.getParamsTypes());
            // 通过反射调用

            Object[] dataArr = new Object[request.getParamsArr().length];
            for (int i = 0; i < request.getParamsArr().length; i++) {
                dataArr[i] = method.invoke(bean, request.getParamsArr()[i]);
            }
            response.setDataArr(dataArr);
            response.setResponseType(TrpcResponseType.TRPC_MULTIPLE_RESPONSE.getCode());
            response.setCode(TrpcResponseCode.SUCCESS.getCode());
            response.setMsg("success");
            response.setRequestId(request.getRequestId());
            response.setReturnType(method.getReturnType());
            chain.doFilter(request, response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean isEnable() {
        return true;
    }

    @Override
    public String order() {
        return TrpcServerFiltersOrder.TrpcDealTrpcMultipleRequestFilter.getOrder();
    }


}
