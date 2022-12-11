package tong.trpc.core;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import tong.trpc.core.domain.*;
import tong.trpc.core.io.client.TrpcClient;
import tong.trpc.core.io.protocol.TrpcTransportProtocol;
import tong.trpc.core.io.protocol.TrpcTransportProtocolHeader;
import tong.trpc.core.io.serialize.TrpcSerialType;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

@Slf4j
@Setter
@Getter
@Data
@NoArgsConstructor
public class TrpcInvocation<T> {

    private long timeout;

    /**
     * 注册中心服务实例的名称
     */
    private String serviceInstanceName;

    /**
     * 服务接口名，接口的全限定名
     */
    private String serviceInterfaceName;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 方法入参类型
     */
    private Class<?>[] paramsTypes;

    /**
     * 方法入参
     */
    private Object[] params;

    private boolean isInvoked;

    private long requestId;

    private TrpcRequest request;

    private TrpcResponse response;

    private TrpcTransportProtocol<TrpcRequest> requestProtocol;

    private CompletableFuture<TrpcResponse> responseFuture;

    private TrpcSerialType serialType;

    private void sendRequest() throws InterruptedException {
        TrpcClient client = new TrpcClient("127.0.0.1", 9000);
        this.requestId = TrpcRequestHolder.REQUEST_ID.incrementAndGet();
        this.request = new TrpcRequest();
        this.request.setClassName(this.serviceInterfaceName);
        this.request.setMethodName(this.methodName);
        this.request.setParamsTypes(this.paramsTypes);
        this.request.setParams(this.params);
        TrpcTransportProtocolHeader header = new TrpcTransportProtocolHeader(
                TrpcConstant.MAGIC, this.serialType.getCode(), TrpcRequestType.REQUEST.getCode(),
                requestId, 0);

        this.requestProtocol = new TrpcTransportProtocol<>();
        this.requestProtocol.setHeader(header);
        this.requestProtocol.setContent(request);

        this.responseFuture = new CompletableFuture<>();
        TrpcRequestHolder.REQUEST_MAP.put(requestId, new TrpcFutureDecorator(this.responseFuture));
        this.isInvoked = true;
        client.sendRequest(this.requestProtocol);
    }

    public T sync() {
        try {
            this.sendRequest();
            this.response = responseFuture.get(this.timeout, TimeUnit.MILLISECONDS);
            TrpcResponse trpcResponse = this.response;
            if (trpcResponse.getCode() == TrpcResponseCode.ERROR.getCode()) {
                throw new RuntimeException("调用发生错误" + trpcResponse.toString());
            }
            return (T) trpcResponse.getData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public CompletableFuture<T> future() {
        try {
            this.sendRequest();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        CompletableFuture<T> future = new CompletableFuture<>();
        this.responseFuture.whenComplete(new BiConsumer<TrpcResponse, Throwable>() {
            @Override
            public void accept(TrpcResponse trpcResponse, Throwable throwable) {
                if (trpcResponse.getCode() == TrpcResponseCode.ERROR.getCode()) {
                    future.completeExceptionally(new RuntimeException("调用失败"));
                } else if (trpcResponse.getCode() == TrpcResponseCode.SUCCESS.getCode()) {
                    future.complete((T)trpcResponse.getData());
                } else {
                    future.completeExceptionally(new RuntimeException("调用失败"));
                }
            }
        });
        return future;
    }


    public void callback(CallBack<T> callBack) {
        try {
            this.sendRequest();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.responseFuture.whenComplete(new BiConsumer<TrpcResponse, Throwable>() {
            @Override
            public void accept(TrpcResponse trpcResponse, Throwable throwable) {
                if (trpcResponse.getCode() == TrpcResponseCode.ERROR.getCode()) {
                    callBack.onFailure(new RuntimeException("调用失败"));
                } else if (trpcResponse.getCode() == TrpcResponseCode.SUCCESS.getCode()) {
                    callBack.onSuccess((T)trpcResponse.getData());
                } else {
                    callBack.onFailure(new RuntimeException("调用失败"));
                }
            }
        });
    }


    public interface CallBack<E> {

        public void onSuccess(E e);

        public void onFailure(Exception e);
    }


}
