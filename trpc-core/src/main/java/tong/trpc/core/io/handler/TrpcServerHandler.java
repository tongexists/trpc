package tong.trpc.core.io.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import tong.trpc.core.domain.*;
import tong.trpc.core.domain.request.TrpcRequest;
import tong.trpc.core.domain.response.TrpcResponse;
import tong.trpc.core.filter.TrpcServerFilters;

@Slf4j
public class TrpcServerHandler extends SimpleChannelInboundHandler<TrpcTransportProtocol<TrpcRequest>> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TrpcTransportProtocol<TrpcRequest> msg) throws Exception {
        TrpcTransportProtocol<TrpcResponse> resProtocol = new TrpcTransportProtocol<>();
        // 处理服务端返回对象
        TrpcTransportProtocolHeader header = msg.getHeader();
        header.setRequestType(TrpcMessageType.RESPONSE.getCode());
        TrpcResponse response = msg.getBody().getContent().newResponse();
        TrpcServerFilters.doFilter(msg.getBody().getContent(), response);
        resProtocol.setHeader(header);
        resProtocol.setBody(new TrpcTransportProtocolBody<>(response));
        ctx.writeAndFlush(resProtocol);
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
