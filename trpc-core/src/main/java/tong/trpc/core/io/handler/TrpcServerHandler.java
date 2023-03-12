package tong.trpc.core.io.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import tong.trpc.core.domain.*;
import tong.trpc.core.spring.SpringBeanManager;

import java.lang.reflect.Method;

@Slf4j
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
            Method method = bean.getClass().getDeclaredMethod(request.getMethodName(), request.getParamsTypes());
            // 通过反射调用
            Object result =  method.invoke(bean, request.getParams());
            TrpcResponse response = new TrpcResponse();
            response.setCode(TrpcResponseCode.SUCCESS.getCode());
            response.setData(result);
            response.setMsg("success");
            response.setReturnType(method.getReturnType().getTypeName());
            return response;
        } catch (Exception e) {
            log.error("", e);
            TrpcResponse response = new TrpcResponse();
            response.setCode(TrpcResponseCode.ERROR.getCode());
            response.setMsg(e.getMessage());
            return response;
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state()== IdleState.READER_IDLE){
                log.info(String.format("客户端[%s]超过指定时间未通信，关闭通道", ctx.channel().remoteAddress().toString()));
                ctx.channel().close();
            }
        }
        ctx.fireUserEventTriggered(evt);
    }
}
