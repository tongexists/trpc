package tong.trpc.core.invocation;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import tong.trpc.core.domain.*;
import tong.trpc.core.domain.request.TrpcRequest;
import tong.trpc.core.domain.request.TrpcRequestImpl;
import tong.trpc.core.domain.response.TrpcResponse;
import tong.trpc.core.io.serialize.TrpcSerialType;
import tong.trpc.core.TrpcInvocationUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 调用实例
 * @param <T> 返回值的类型
 */
@Slf4j
@Setter
@Getter
@Data
@NoArgsConstructor
public class TrpcInvocation<T> {
    /**
     * 超时，秒单位
     */
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
    /**
     * 是否已经调用过
     */
    private boolean isInvoked;
    /**
     * 请求id
     */
    private long requestId;
    /**
     * 请求
     */
    private TrpcRequest request = new TrpcRequestImpl();
    /**
     * 响应
     */
    private TrpcResponse response;
    /**
     * 协议
     */
    private TrpcTransportProtocol<TrpcRequest> requestProtocol;
    /**
     * 响应的CompletableFuture
     */
    private CompletableFuture<TrpcResponse> responseFuture;
    /**
     * 序列化类型
     */
    private TrpcSerialType serialType;
    /**
     * 请求类型
     */
    private int requestType;
    /**
     * 配置TrpcRequest
     */
    private Consumer<TrpcRequest> configRequestConsumer = (a) -> {};

    /**
     * 发起请求，装配TrpcRequest、协议，经客户端过滤器处理后，发起请求
     * @throws InterruptedException
     */
    void sendRequest() throws InterruptedException {
        this.requestId = TrpcInvocationUtils.newRequestId();
        this.request.setRequestType(this.requestType);
        this.request.setClassName(this.serviceInterfaceName);
        this.request.setMethodName(this.methodName);
        this.request.setParamsTypes(this.paramsTypes);
        this.request.setRequestId(requestId);
        this.request.setParams(this.params);
        this.request.setServiceInstanceName(this.serviceInstanceName);
        this.configRequestConsumer.accept(this.request);
        TrpcTransportProtocolHeader header = new TrpcTransportProtocolHeader(
                TrpcConstant.MAGIC, this.serialType.getCode(), TrpcMessageType.REQUEST.getCode(),
                requestId, 0);
        TrpcTransportProtocolBody<TrpcRequest> body = new TrpcTransportProtocolBody<>(request);
        this.requestProtocol = new TrpcTransportProtocol<>();
        this.requestProtocol.setHeader(header);
        this.requestProtocol.setBody(body);

        //已处理
        this.isInvoked = true;
        this.responseFuture = TrpcInvocationUtils.invokeByProtocol(this.requestProtocol);

    }

    /**
     * 配置TrpcRequest
     * @param consumer
     */
    public TrpcInvocation<T> configRequest(Consumer<TrpcRequest> consumer) {
        this.configRequestConsumer = consumer;
        return this;
    }

    /**
     * 配置Invocation
     * @param consumer
     */
    public TrpcInvocation<T> configInvocation(Consumer<TrpcInvocation<T>> consumer) {
        consumer.accept(this);
        return this;
    }

    /**
     * 批量调用，转化为TrpcMultipleInvocation
     * @param paramsArr 调用多次的实参数组
     * @return  TrpcMultipleInvocation
     */
    public TrpcMultipleInvocation<T> multipleCall(Object[]... paramsArr) {
        return transfer(new Function<TrpcInvocation<T>, TrpcMultipleInvocation<T>>() {
            @Override
            public TrpcMultipleInvocation<T> apply(TrpcInvocation<T> tTrpcInvocation) {
                tTrpcInvocation.requestType = TrpcRequestType.TRPC_MULTIPLE_REQUEST.getCode();
                return new TrpcMultipleInvocation<>(tTrpcInvocation, paramsArr);
            }
        });
    }

    /**
     * 转换为你想要的类型
     * @param function
     * @return
     * @param <E> 你想要的类型
     */
    public <E> E transfer(Function<TrpcInvocation<T>, E> function) {
        return function.apply(this);
    }

    /**
     * 同步阻塞获取返回值
     * @return 返回值
     */
    public T sync() {
        try {
            //发生请求
            this.sendRequest();
            //获取到响应
            this.response = responseFuture.get(this.timeout, TimeUnit.MILLISECONDS);
            TrpcResponse trpcResponse = this.response;
            if (trpcResponse.getCode() == TrpcResponseCode.ERROR.getCode()) {
                throw new RuntimeException("调用发生错误" + trpcResponse.toString());
            }
            return dealDifferenceBetweenClassloader(trpcResponse.getData());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 仅是转换，无大作用，不要被名字吓到了
     * @param raw
     * @return
     */
    private T dealDifferenceBetweenClassloader(Object raw) {
        return (T)raw;
    }

    /**
     * 将响应包装到新的CompletableFuture，并返回
     * @return 新的CompletableFuture
     */
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
                    future.complete(dealDifferenceBetweenClassloader(trpcResponse.getData()));
                } else {
                    future.completeExceptionally(new RuntimeException("调用失败"));
                }
            }
        });
        return future;
    }

    /**
     * 回调
     * @param callBack 回调
     */
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
                    callBack.onSuccess(dealDifferenceBetweenClassloader(trpcResponse.getData()));
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
