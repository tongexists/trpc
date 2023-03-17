package tong.trpc.core.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrpcResponseImpl extends AbstractTrpcResponse {


    private Class<?> returnType;

}
