package tong.trpc.core.io.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import tong.trpc.core.domain.TrpcTransportProtocol;
import tong.trpc.core.domain.TrpcTransportProtocolHeader;
import tong.trpc.core.io.serialize.ITrpcSerializer;
import tong.trpc.core.io.serialize.TrpcSerializerManager;

@Slf4j
public class TrpcEncoder extends MessageToByteEncoder<TrpcTransportProtocol> {
    @Override
    protected void encode(ChannelHandlerContext ctx, TrpcTransportProtocol msg, ByteBuf out) throws Exception {
        TrpcTransportProtocolHeader header = msg.getHeader();
        out.writeShort(header.getMagic());
        out.writeByte(header.getSerialType());
        out.writeByte(header.getRequestType());
        out.writeLong(header.getRequestId());
        // 序列化内容
        ITrpcSerializer serializer = TrpcSerializerManager.getSerializer(header.getSerialType());
        log.debug(serializer.getClass().getName() + " serialize...");
        byte[] data = null;
        data = serializer.serialize(msg.getContent());
        out.writeInt(data.length);
        out.writeBytes(data);
    }
}
