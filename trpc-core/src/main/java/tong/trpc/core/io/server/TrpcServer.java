package tong.trpc.core.io.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import tong.trpc.core.io.protocol.TrpcDecoder;
import tong.trpc.core.io.protocol.TrpcEncoder;

@Slf4j
public class TrpcServer {

    private String serverAddress; //服务地址
    private int serverPort; //端口

    public TrpcServer(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void startNettyServer() {
        log.info("begin start Netty server");
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

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
                                .addLast(new TrpcDecoder())
                                .addLast(new TrpcEncoder())
                                .addLast(new TrpcServerHandler());
                    }
                });
        try {
            ChannelFuture future = bootstrap.bind(this.serverAddress, this.serverPort).sync();
            log.info("Server started Success on Port" + this.serverPort);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
