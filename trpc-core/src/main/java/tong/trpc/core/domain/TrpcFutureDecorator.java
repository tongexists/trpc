package tong.trpc.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.CompletableFuture;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrpcFutureDecorator {

    private CompletableFuture<TrpcResponse> responseFuture;

//    private CompletableFuture resultFuture;

//    private Class<?> resultClazz;



}
