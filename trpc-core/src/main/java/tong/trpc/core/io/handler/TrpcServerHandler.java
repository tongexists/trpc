package tong.trpc.core.io.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import tong.trpc.core.domain.*;
import tong.trpc.core.spring.SpringBeanManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class TrpcServerHandler extends SimpleChannelInboundHandler<TrpcTransportProtocol<TrpcRequest>> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TrpcTransportProtocol<TrpcRequest> msg) throws Exception {
        TrpcTransportProtocol<TrpcResponse> resProtocol = new TrpcTransportProtocol<>();
        // 处理服务端返回对象
        TrpcTransportProtocolHeader header = msg.getHeader();
        header.setRequestType(TrpcRequestType.RESPONSE.getCode());
        // 通过反射调用对应方法
        TrpcResponse response = invoke(msg.getContent());

        resProtocol.setHeader(header);


        resProtocol.setContent(response);

        ctx.writeAndFlush(resProtocol);
    }

    // 通过反射进行调用
    private TrpcResponse invoke(TrpcRequest request) {
        try {
            // 反射加载
            Class<?> clazz = Class.forName(request.getClassName());
            // 加载实例
            Object bean = SpringBeanManager.getBean(clazz);
            // 加载实例调用的方法
            Class<?>[] paramTypes = Arrays.stream(request.getParamsTypes()).map(clsStr -> {
                try {
                    return Class.forName(clsStr);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }).toArray(Class[]::new);
            Method method = clazz.getDeclaredMethod(request.getMethodName(), paramTypes);
            // 通过反射调用
            Object result =  method.invoke(bean, request.getParams());
            TrpcResponse response = new TrpcResponse();
            response.setCode(TrpcResponseCode.SUCCESS.getCode());
            response.setData(result);
            response.setMsg("success");
            response.setReturnType(method.getReturnType().getTypeName());
            return response;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
