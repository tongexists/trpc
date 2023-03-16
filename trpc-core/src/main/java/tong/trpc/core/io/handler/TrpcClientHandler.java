package tong.trpc.core.io.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import tong.trpc.core.domain.*;
import tong.trpc.core.io.serialize.TrpcSerialType;

@Slf4j
public class TrpcClientHandler extends SimpleChannelInboundHandler<TrpcTransportProtocol<TrpcResponse>> {




    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TrpcTransportProtocol<TrpcResponse> msg) throws Exception {
        log.debug("receive Rpc Server Result");
        long requestId = msg.getHeader().getRequestId();
        //根据ID获得异步对象
        TrpcFutureDecorator decorator = TrpcRequestHolder.REQUEST_MAP.remove(requestId);
        TrpcResponse response = msg.getContent();
        if (response.getCode() == TrpcResponseCode.SUCCESS.getCode()) {
            decorator.getResponseFuture().complete(msg.getContent()); //返回结果
        } else {
            decorator.getResponseFuture().completeExceptionally(new RuntimeException(response.toString()));
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state()== IdleState.WRITER_IDLE){
                log.debug(String.format("%d秒心跳异常",TrpcConstant.HEART_BEAT_INTERNAL));
                TrpcTransportProtocolHeader header = new TrpcTransportProtocolHeader(TrpcConstant.MAGIC,
                        TrpcSerialType.TrpcKryoSerializer.getCode(), TrpcRequestType.HEARTBEAT.getCode(),
                        TrpcRequestHolder.REQUEST_ID.incrementAndGet(), 0
                        );
                TrpcTransportProtocol protocol = new TrpcTransportProtocol(header, "");
                ctx.writeAndFlush(protocol);
            }
        }
        ctx.fireUserEventTriggered(evt);
    }
}
