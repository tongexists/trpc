package tong.trpc.core.domain.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TrpcResponseImpl extends AbstractTrpcResponse {


    private Class<?> returnType;

}
