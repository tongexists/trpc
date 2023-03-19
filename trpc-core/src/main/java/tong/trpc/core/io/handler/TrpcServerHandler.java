package tong.trpc.core.io.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import tong.trpc.core.domain.*;
import tong.trpc.core.domain.request.TrpcRequest;
import tong.trpc.core.domain.response.TrpcResponse;
import tong.trpc.core.filter.server.TrpcServerFilters;

/**
 * 服务端处理器
 */
@Slf4j
public class TrpcServerHandler extends SimpleChannelInboundHandler<TrpcTransportProtocol<TrpcRequest>> {

    /**
     * 装配协议TrpcTransportProtocol返回。请求需要经过TrpcServerFilters的处理
     * @param ctx           the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler}
     *                      belongs to
     * @param msg           the message to handle
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TrpcTransportProtocol<TrpcRequest> msg) throws Exception {
        TrpcTransportProtocol<TrpcResponse> resProtocol = new TrpcTransportProtocol<>();
        // 处理服务端返回对象
        TrpcTransportProtocolHeader header = msg.getHeader();
        header.setRequestType(TrpcMessageType.RESPONSE.getCode());
        TrpcResponse response = msg.getBody().getContent().newResponse();
        //过滤器处理
        TrpcServerFilters.doFilter(msg.getBody().getContent(), response);
        resProtocol.setHeader(header);
        resProtocol.setBody(new TrpcTransportProtocolBody<>(response));
        ctx.writeAndFlush(resProtocol);
    }

    /**
     * 客户端心跳超时，主动断开连接
     * @param ctx
     * @param evt
     * @throws Exception
     */
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
