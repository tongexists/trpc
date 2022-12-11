package tong.trpc.core.io.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import tong.trpc.core.domain.TrpcRequest;
import tong.trpc.core.domain.TrpcRequestType;
import tong.trpc.core.io.serialize.ITrpcSerializer;
import tong.trpc.core.io.serialize.TrpcSerializerManager;

public class TrpcEncoder extends MessageToByteEncoder<TrpcTransportProtocol<TrpcRequest>> {
    @Override
    protected void encode(ChannelHandlerContext ctx, TrpcTransportProtocol msg, ByteBuf out) throws Exception {
        TrpcTransportProtocolHeader header = msg.getHeader();
        out.writeShort(header.getMagic());
        out.writeByte(header.getSerialType());
        out.writeByte(header.getRequestType());
        out.writeLong(header.getRequestId());
        // 序列化内容
        ITrpcSerializer serializer = TrpcSerializerManager.getSerializer(header.getSerialType());
        byte[] data = null;
        data = serializer.serialize(msg.getContent());
        out.writeInt(data.length);
        out.writeBytes(data);
    }
}
