package tong.trpc.core.io.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import tong.trpc.core.TrpcConfig;
import tong.trpc.core.domain.*;
import tong.trpc.core.domain.response.TrpcResponse;
import tong.trpc.core.exception.TrpcInvocationException;
import tong.trpc.core.io.TrpcClient;
import tong.trpc.core.io.serialize.TrpcSerialType;

/**
 * 客户端io处理器
 */
@Slf4j
public class TrpcClientHandler extends SimpleChannelInboundHandler<TrpcTransportProtocol<TrpcResponse>> {

    private TrpcClient client;
    public TrpcClientHandler(TrpcClient client) {
        this.client = client;
    }
    /**
     * 根据请求id获得异步对象，根据响应码来决定是成功还是异常完成
     * @param ctx           the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler}
     *                      belongs to
     * @param msg           the message to handle
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TrpcTransportProtocol<TrpcResponse> msg) throws Exception {
        log.debug("receive Rpc Server Result");
        long requestId = msg.getHeader().getRequestId();
        //根据ID获得异步对象
        TrpcFutureDecorator decorator = TrpcRequestHolder.REQUEST_MAP.remove(requestId);
        TrpcResponse response = msg.getBody().getContent();
        // 成功
        if (response.getCode() == TrpcResponseCode.SUCCESS.getCode()) {
            decorator.getResponseFuture().complete(msg.getBody().getContent());
        // 异常
        } else {
            decorator.getResponseFuture().completeExceptionally(new TrpcInvocationException(response.toString(), null));
        }
    }

    /**
     * 间隔发起心跳
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state()== IdleState.WRITER_IDLE){
                if (System.currentTimeMillis() - this.client.getLastWriteTime() > TrpcConfig.clientWriteIdleThreshold) {
                    log.info("客户端写操作空闲时间超过{}ms，即将关闭客户端",TrpcConfig.clientWriteIdleThreshold);
                    this.client.close();
                } else {
                    log.debug(String.format("隔%d秒发起心跳",TrpcConstant.HEART_BEAT_INTERNAL));
                    TrpcTransportProtocolHeader header = new TrpcTransportProtocolHeader(TrpcConstant.MAGIC,
                            TrpcSerialType.TrpcKryoSerializer.getCode(), TrpcMessageType.HEARTBEAT.getCode(),
                            TrpcRequestHolder.REQUEST_ID.incrementAndGet(), 0
                    );
                    TrpcTransportProtocol protocol = new TrpcTransportProtocol(header, new TrpcTransportProtocolBody());
                    ctx.writeAndFlush(protocol);
                }
            }
        }
        ctx.fireUserEventTriggered(evt);
    }
}
