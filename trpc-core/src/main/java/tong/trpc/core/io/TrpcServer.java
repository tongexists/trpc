package tong.trpc.core.io;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import tong.trpc.core.TrpcConfig;
import tong.trpc.core.domain.TrpcConstant;
import tong.trpc.core.io.handler.ExceptionHandler;
import tong.trpc.core.io.handler.TrpcDecoder;
import tong.trpc.core.io.handler.TrpcEncoder;
import tong.trpc.core.io.handler.TrpcServerHandler;

import java.util.concurrent.TimeUnit;

/**
 * 服务端
 */
@Slf4j
public class TrpcServer {

    //服务地址
    private String serverAddress;
    //端口
    private int serverPort;

    /**
     *
     * @param serverAddress 服务地址
     * @param serverPort 端口
     */
    public TrpcServer(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    /**
     * 启动netty server
     * 初始化ServerBootstrap， 绑定地址端口
     */
    public void startNettyServer() {
        log.debug("begin start Netty server");
        EventLoopGroup boss = new NioEventLoopGroup(TrpcConfig.serverAcceptRequestThreads);
        EventLoopGroup worker = new NioEventLoopGroup(TrpcConfig.serverWorkThreads);

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {

                        socketChannel.pipeline().
                                addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,
                                        12,
                                        4,
                                        0,
                                        0))
                                .addLast(new LoggingHandler())
                                .addLast(new IdleStateHandler(TrpcConstant.IDLE_THRESHOLD, 0, 0, TimeUnit.SECONDS))
                                .addLast(new TrpcDecoder())
                                .addLast(new TrpcEncoder())
                                .addLast(new TrpcServerHandler())
                                .addLast(new ExceptionHandler());
                    }
                });
        try {
            ChannelFuture future = bootstrap.bind(this.serverAddress, this.serverPort).sync();
            log.info("Server started Success on Port" + this.serverPort);
            future.channel().closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    log.info("关闭netty server");
                    boss.shutdownGracefully();
                    worker.shutdownGracefully();
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException("Server startup failed");
        }
    }
}
