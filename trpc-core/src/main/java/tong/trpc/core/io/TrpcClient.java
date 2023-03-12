package tong.trpc.core.io;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import tong.trpc.core.discovery.TrpcDiscovery;
import tong.trpc.core.domain.TrpcConstant;
import tong.trpc.core.domain.TrpcTransportProtocol;
import tong.trpc.core.exception.ServerCloseConnectionException;
import tong.trpc.core.io.handler.ExceptionHandler;
import tong.trpc.core.io.handler.TrpcClientHandler;
import tong.trpc.core.io.handler.TrpcDecoder;
import tong.trpc.core.io.handler.TrpcEncoder;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TrpcClient {

    private final Bootstrap bootstrap;

    private final EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

    private String serviceAddress;

    private int servicePort;

    private Channel channel;

    public static ConcurrentHashMap<String, TrpcClient> clientPool = new ConcurrentHashMap<>();
    public TrpcClient(String serviceAddress, int servicePort) {
        log.info("begin init Netty Client,{},{}", serviceAddress, servicePort);
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
                                .addLast(new IdleStateHandler(0, TrpcConstant.HEART_BEAT_INTERNAL,0, TimeUnit.SECONDS))
                                .addLast(new TrpcDecoder())
                                .addLast(new TrpcEncoder())
                                .addLast(new TrpcClientHandler())
                                .addLast(new ExceptionHandler());
                    }
                });

        this.serviceAddress = serviceAddress;
        this.servicePort = servicePort;

    }

    public boolean connect() {
        final ChannelFuture future;
        try {
            future = bootstrap.connect(this.serviceAddress, this.servicePort).sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //监听是否连接成功
        if (future.isSuccess()) {
            log.info("connect rpc server {}:{} success.", this.serviceAddress, this.servicePort);
            this.channel = future.channel();
            return true;
        } else {
            log.error("connect rpc server {}:{} failed. ", this.serviceAddress, this.servicePort);
            future.cause().printStackTrace();
            eventLoopGroup.shutdownGracefully();
            return false;
        }
    }

    // 发送数据包
    public void sendRequest(TrpcTransportProtocol protocol) throws ServerCloseConnectionException {
        log.debug("begin transfer data");
        if (!this.channel.isActive()) {
            throw new ServerCloseConnectionException(String.format("与服务端[%s]的连接已断开", this.channel.remoteAddress().toString()));
        }
        this.channel.writeAndFlush(protocol);
    }

    public boolean isActive() {
        return this.channel.isActive();
    }

    public static void sendRequest(TrpcTransportProtocol protocol, String serviceInstanceName) {
        TrpcDiscovery discovery = TrpcDiscovery.getDiscovery();
        String addressPort = discovery.getInstance(serviceInstanceName);
        String[] split = addressPort.split(":");
        TrpcClient client = TrpcClient.clientPool.get(addressPort);
        if (client == null) {
            client = new TrpcClient(split[0], Integer.parseInt(split[1]));
            if (client.connect()) {
                log.info("连接远程服务成功");
            } else {
                log.error("连接远程服务失败");
                throw new RuntimeException("连接远程服务失败");
            }
            TrpcClient.clientPool.put(addressPort, client);
        } else {
            if (!client.isActive()) {
                if (client.connect()) {
                    log.info("连接远程服务成功");
                } else {
                    log.error("连接远程服务失败");
                    throw new RuntimeException("连接远程服务失败");
                }
            }
        }

        try {
            client.sendRequest(protocol);
        } catch (ServerCloseConnectionException e) {
            throw new RuntimeException(e);
        }

    }
}
