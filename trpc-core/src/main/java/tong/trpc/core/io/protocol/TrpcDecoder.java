package tong.trpc.core.io.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import tong.trpc.core.domain.TrpcConstant;
import tong.trpc.core.domain.TrpcRequest;
import tong.trpc.core.domain.TrpcRequestType;
import tong.trpc.core.domain.TrpcResponse;
import tong.trpc.core.io.serialize.ITrpcSerializer;
import tong.trpc.core.io.serialize.TrpcSerializerManager;

import java.util.List;
@Slf4j
public class TrpcDecoder extends ByteToMessageDecoder {


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        log.info("========begin RpcDecoder==========");

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
        TrpcRequestType rt = TrpcRequestType.findByCode(reqType);//获得请求类型
        switch (rt) {
            case REQUEST:
                // 将内容反序列化
                TrpcRequest request = serializer.deserialize(content, TrpcRequest.class);
                // 最好的返回体
                TrpcTransportProtocol<TrpcRequest> reqProtocol = new TrpcTransportProtocol();
                reqProtocol.setHeader(header);
                reqProtocol.setContent(request);
                // 传递
                out.add(reqProtocol);
                break;
            case RESPONSE:
                TrpcResponse response = serializer.deserialize(content, TrpcResponse.class);
                TrpcTransportProtocol<TrpcResponse> resProtocol = new TrpcTransportProtocol();
                resProtocol.setHeader(header);
                resProtocol.setContent(response);
                out.add(resProtocol);
                break;
            case HEARTBEAT:
                //TODO
                break;
            default:
                break;
        }

    }
}