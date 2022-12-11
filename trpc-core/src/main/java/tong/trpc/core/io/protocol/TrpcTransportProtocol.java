package tong.trpc.core.io.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrpcTransportProtocol<T> {

    private TrpcTransportProtocolHeader header;

    private T content;
}
