package tong.trpc.core.io.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import tong.trpc.core.domain.TrpcFutureDecorator;
import tong.trpc.core.domain.TrpcRequest;
import tong.trpc.core.domain.TrpcRequestHolder;
import tong.trpc.core.domain.TrpcResponse;
import tong.trpc.core.io.protocol.TrpcTransportProtocol;
@Slf4j
public class TrpcClientHandler extends SimpleChannelInboundHandler<TrpcTransportProtocol<TrpcResponse>> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TrpcTransportProtocol<TrpcResponse> msg) throws Exception {
        log.info("receive Rpc Server Result");
        long requestId = msg.getHeader().getRequestId();
        //根据ID获得异步对象
        TrpcFutureDecorator decorator = TrpcRequestHolder.REQUEST_MAP.remove(requestId);
        decorator.getResponseFuture().complete(msg.getContent()); //返回结果
//        decorator.getResultFuture().complete(msg.getContent().getData());
    }
}
