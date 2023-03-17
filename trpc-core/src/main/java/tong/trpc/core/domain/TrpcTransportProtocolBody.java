package tong.trpc.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrpcTransportProtocolBody<T> {

    private T content;
}
