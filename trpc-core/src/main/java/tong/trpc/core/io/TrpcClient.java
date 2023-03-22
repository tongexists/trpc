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
import tong.trpc.core.TrpcConfig;
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

/**
 * 客户端
 */
@Slf4j
public class TrpcClient {
    /**
     * netty的Bootstrap
     */
    private final Bootstrap bootstrap;
    /**
     * netty的EventLoopGroup
     */
    private final EventLoopGroup eventLoopGroup;
    /**
     * 远程服务端的地址
     */
    private String serviceAddress;
    /**
     * 远程服务端的端口
     */
    private int servicePort;
    /**
     * 通道，用于写数据
     */
    private Channel channel;
    /**
     * 远程服务端的服务名 -》 客户端
     */
    public static ConcurrentHashMap<String, TrpcClient> clientPool = new ConcurrentHashMap<>();

    private static ConcurrentHashMap<String, Object> clientLockMap = new ConcurrentHashMap<>();

    /**
     * 构造客户端，初始化netty客户端
     * @param serviceAddress 远程服务端的地址
     * @param servicePort 远程服务端的端口
     */
    public TrpcClient(String serviceAddress, int servicePort) {
        log.info("begin init Netty Client,{},{}", serviceAddress, servicePort);
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup(TrpcConfig.clientWorkThreads);
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

    /**
     * 连接
     * @return 是否成功
     */
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

    /**
     * 发送数据
     * @param protocol 协议
     * @throws ServerCloseConnectionException 当服务端认为客户端心跳异常时会断开连接，此时发送数据会抛出ServerCloseConnectionException
     */
    public void sendRequest(TrpcTransportProtocol protocol) throws ServerCloseConnectionException {
        log.debug("begin transfer data");
        if (!this.channel.isActive()) {
            throw new ServerCloseConnectionException(String.format("与服务端[%s]的连接已断开", this.channel.remoteAddress().toString()));
        }
        this.channel.writeAndFlush(protocol);
    }

    /**
     * 与服务端的连接是否活跃
     * @return 与服务端的连接是否活跃
     */
    public boolean isActive() {
        return this.channel.isActive();
    }

    private static Object addClientLock(String addressPort) {
        if (!clientLockMap.containsKey(addressPort)) {
            Object o = new Object();
            clientLockMap.put(addressPort, o);
            return o;
        }
        return clientLockMap.get(addressPort);
    }

    /**
     * 服务发现服务名下的一个服务实例，连接上服务端，并发送数据
     * @param protocol 协议
     * @param serviceInstanceName 服务名
     */
    public static void sendRequest(TrpcTransportProtocol protocol, String serviceInstanceName) {
        TrpcDiscovery discovery = TrpcDiscovery.getDiscovery();
        // 负载均衡选到一个服务实例
        String addressPort = discovery.getInstance(serviceInstanceName);
        String[] split = addressPort.split(":");
        // 从池中找一下，看是否已经连接过
        TrpcClient client = TrpcClient.clientPool.get(addressPort);
        Object lock = addClientLock(addressPort);
        //未连接
        if (client == null) {
            synchronized (lock) {
                client = TrpcClient.clientPool.get(addressPort);
                if (client == null) {
                    client = new TrpcClient(split[0], Integer.parseInt(split[1]));
                    //连接
                    if (client.connect()) {
                        log.info("连接远程服务成功");
                    } else {
                        log.error("连接远程服务失败");
                        throw new RuntimeException("连接远程服务失败");
                    }
                    TrpcClient.clientPool.put(addressPort, client);
                }
            }

        // 已连接
        } else {
            //连接已被关闭，重新连接
            if (!client.isActive()) {
                if (client.connect()) {
                    log.info("连接远程服务成功");
                } else {
                    log.error("连接远程服务失败");
                    throw new RuntimeException("连接远程服务失败");
                }
            }
        }
        //发送数据
        try {
            client.sendRequest(protocol);
        } catch (ServerCloseConnectionException e) {
            throw new RuntimeException(e);
        }

    }
}
