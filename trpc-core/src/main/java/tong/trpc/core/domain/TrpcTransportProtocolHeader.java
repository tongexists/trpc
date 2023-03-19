package tong.trpc.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 协议头
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrpcTransportProtocolHeader  implements Serializable {

    private short magic; // 魔数 2个字节

    private byte serialType; //序列化类型 1个字节

    private byte requestType; // 消息类型 1个字节

    private long requestId; // 请求ID 8个字节

    private int length; //消息体长度 4个字节

}

