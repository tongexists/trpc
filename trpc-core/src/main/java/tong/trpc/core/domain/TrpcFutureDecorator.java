package tong.trpc.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tong.trpc.core.domain.response.TrpcResponse;

import java.util.concurrent.CompletableFuture;

/**
 * 单纯用于包装CompletableFuture<TrpcResponse>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrpcFutureDecorator {
    /**
     * 响应的CompletableFuture
     */
    private CompletableFuture<TrpcResponse> responseFuture;

}
