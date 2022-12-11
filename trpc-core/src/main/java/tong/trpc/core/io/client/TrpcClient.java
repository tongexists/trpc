package tong.trpc.core.io.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import tong.trpc.core.domain.TrpcRequest;
import tong.trpc.core.io.protocol.TrpcDecoder;
import tong.trpc.core.io.protocol.TrpcEncoder;
import tong.trpc.core.io.protocol.TrpcTransportProtocol;
@Slf4j
public class TrpcClient {

    private final Bootstrap bootstrap;

    private final EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

    private String serviceAddress;

    private int servicePort;

    public TrpcClient(String serviceAddress, int servicePort) {
        System.out.printf("begin init Netty Client,{},{}", serviceAddress, servicePort);
        bootstrap = new Bootstrap();

        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(
                                        new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,
                                                12,
                                                4,
                                                0, 0))
                                .addLast(new LoggingHandler()) // 日志处理
                                .addLast(new TrpcEncoder())
                                .addLast(new TrpcDecoder())
                                .addLast(new TrpcClientHandler());
                    }
                });

        this.serviceAddress = serviceAddress;
        this.servicePort = servicePort;
    }

    // 发送数据包
    public void sendRequest(TrpcTransportProtocol protocol) throws InterruptedException {
        final ChannelFuture future = bootstrap.connect(this.serviceAddress, this.servicePort).sync();
        //监听是否连接成功
        future.addListener(listener -> {
            if (future.isSuccess()) {
                System.out.printf("connect rpc server {} success.", this.serviceAddress);
            } else {
                System.out.printf("connect rpc server {} failed. ", this.serviceAddress);
                future.cause().printStackTrace();
                eventLoopGroup.shutdownGracefully();
            }
        });
        log.info("begin transfer data");
        future.channel().writeAndFlush(protocol);
    }
}
