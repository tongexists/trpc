package tong.trpc.core.io.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author tong-exists
 * @Create 2022/12/12 16:24
 * @Version 1.0
 */
@Slf4j
public class ExceptionHandler extends ChannelDuplexHandler {

    /**
     * exceptionCaught是用来捕获Inbound抛出的异常
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("捕获到入站异常",cause);
    }

    /**
     * 重写write是用来捕获outbound抛出的异常
     * @param ctx
     * @param msg
     * @param promise
     * @throws Exception
     */
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ctx.write(msg, promise.addListener( future -> {
            if (!future.isSuccess()){
                log.error("捕获出站到异常",future.cause());
            }
        }));
    }
}
