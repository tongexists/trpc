package tong.trpc.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrpcTransportProtocol<T> {

    private TrpcTransportProtocolHeader header;

    private TrpcTransportProtocolBody<T> body;
}
