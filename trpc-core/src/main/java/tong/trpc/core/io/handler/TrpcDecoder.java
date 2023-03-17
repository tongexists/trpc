package tong.trpc.core.io.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import tong.trpc.core.domain.*;
import tong.trpc.core.domain.request.TrpcRequest;
import tong.trpc.core.domain.response.TrpcResponse;
import tong.trpc.core.io.serialize.ITrpcSerializer;
import tong.trpc.core.io.serialize.TrpcSerializerManager;

import java.util.List;

@Slf4j
public class TrpcDecoder extends ByteToMessageDecoder {


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        log.debug("========begin RpcDecoder==========");

        if (in.readableBytes() < TrpcConstant.HEAD_TOTAL_LEN) {
            return;
        }
        in.markReaderIndex(); //标记读取开始索引
        short maci = in.readShort(); //读取2个字节的magic
        if (maci != TrpcConstant.MAGIC) {
            throw new IllegalArgumentException("Illegal request parameter 'magic'," + maci);
        }

        byte serialType = in.readByte(); //读取一个字节的序列化类型
        byte reqType = in.readByte(); //读取一个字节的消息类型
        long requestId = in.readLong(); //读取请求id
        int dataLength = in.readInt(); //读取数据报文长度

        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex(); // 还原数据
            return;
        }
        //读取消息体的内容
        byte[] content = new byte[dataLength];
        in.readBytes(content);

        TrpcTransportProtocolHeader header = new TrpcTransportProtocolHeader(maci, serialType, reqType, requestId, dataLength);
        ITrpcSerializer serializer = TrpcSerializerManager.getSerializer(serialType);//获得序列化类型
        TrpcMessageType rt = TrpcMessageType.findByCode(reqType);//获得请求类型
        log.debug(serializer.getClass().getName() + " deserialize...");
        switch (rt) {
            case REQUEST:
                // 将内容反序列化
                TrpcTransportProtocolBody<TrpcRequest> request = serializer.deserialize(content, TrpcTransportProtocolBody.class);
                // 最好的返回体
                TrpcTransportProtocol<TrpcRequest> reqProtocol = new TrpcTransportProtocol<>();
                reqProtocol.setHeader(header);
                reqProtocol.setBody(request);
                // 传递
                out.add(reqProtocol);
                break;
            case RESPONSE:
                // 将内容反序列化
                TrpcTransportProtocolBody<TrpcResponse> body = serializer.deserialize(content, TrpcTransportProtocolBody.class);
                // 最好的返回体
                TrpcTransportProtocol<TrpcResponse> reqProtocol222 = new TrpcTransportProtocol<>();
                reqProtocol222.setHeader(header);
                reqProtocol222.setBody(body);
                // 传递
                out.add(reqProtocol222);
                break;
            case HEARTBEAT:
                break;
            default:
                break;
        }

    }
}
