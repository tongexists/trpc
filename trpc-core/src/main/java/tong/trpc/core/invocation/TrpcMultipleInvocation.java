package tong.trpc.core.invocation;

import tong.trpc.core.domain.TrpcRequestType;
import tong.trpc.core.domain.request.TrpcMultipleRequest;
import tong.trpc.core.domain.response.TrpcMultipleResponse;
import tong.trpc.core.domain.response.TrpcResponse;
import tong.trpc.core.domain.TrpcResponseCode;

import java.lang.reflect.Array;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

/**
 * 批次调用，同一个方法，执行多次调用，需要多个批次实参
 * @Author tong-exists
 * @Create 2023/3/17 11:04
 * @Version 1.0
 */
public class TrpcMultipleInvocation<T>  {
    /**
     * 实参数组
     */
    private Object[][] paramsArr;
    /**
     * 原本的TrpcInvocation
     */
    private TrpcInvocation<T> trpcInvocation;
    /**
     * 对应的TrpcMultipleRequest
     */
    private TrpcMultipleRequest request;

    /**
     * 设置trpcInvocation的TrpcRequest为TrpcMultipleRequest
     * @param trpcInvocation
     * @param paramsArr
     */
    public TrpcMultipleInvocation(TrpcInvocation<T> trpcInvocation, Object[][] paramsArr) {
        this.paramsArr = paramsArr;
        this.trpcInvocation = trpcInvocation;
        this.request = new TrpcMultipleRequest();
        // 设置trpcInvocation的TrpcRequest为TrpcMultipleRequest
        this.trpcInvocation.setRequest(this.request);
        this.request.setRequestType(TrpcRequestType.TRPC_MULTIPLE_REQUEST.getCode());
        this.request.setParamsArr(paramsArr);
    }

    /**
     * 同步阻塞获取结果
     * @return
     */
    public T[] sync() {
        try {
            this.trpcInvocation.sendRequest();
            this.trpcInvocation.setResponse(this.trpcInvocation.getResponseFuture().get(
                    this.trpcInvocation.getTimeout(), TimeUnit.MILLISECONDS
            ));
            TrpcResponse trpcResponse = this.trpcInvocation.getResponse();
            if (trpcResponse.getCode() != TrpcResponseCode.SUCCESS.getCode()) {
                throw new RuntimeException("调用发生错误" + trpcResponse.toString());
            }
            return extractResult(trpcResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将响应包装到新的CompletableFuture，并返回
     * @return
     */
    public CompletableFuture<T[]> future() {
        try {
            this.trpcInvocation.sendRequest();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        CompletableFuture<T[]> future = new CompletableFuture<>();
        this.trpcInvocation.getResponseFuture().whenComplete(new BiConsumer<TrpcResponse, Throwable>() {
            @Override
            public void accept(TrpcResponse trpcResponse, Throwable throwable) {
                if (trpcResponse.getCode() == TrpcResponseCode.ERROR.getCode()) {
                    future.completeExceptionally(new RuntimeException("调用失败"));
                } else if (trpcResponse.getCode() == TrpcResponseCode.SUCCESS.getCode()) {
                    future.complete(extractResult(trpcResponse));
                } else {
                    future.completeExceptionally(new RuntimeException("调用失败"));
                }
            }
        });
        return future;
    }

    /**
     * 回调
     * @param callBack
     */
    public void callback(TrpcInvocation.CallBack<T[]> callBack) {
        try {
            this.trpcInvocation.sendRequest();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.trpcInvocation.getResponseFuture().whenComplete(new BiConsumer<TrpcResponse, Throwable>() {
            @Override
            public void accept(TrpcResponse trpcResponse, Throwable throwable) {
                if (trpcResponse.getCode() == TrpcResponseCode.ERROR.getCode()) {
                    callBack.onFailure(new RuntimeException("调用失败"));
                } else if (trpcResponse.getCode() == TrpcResponseCode.SUCCESS.getCode()) {
                    callBack.onSuccess(extractResult(trpcResponse));
                } else {
                    callBack.onFailure(new RuntimeException("调用失败"));
                }
            }
        });
    }

    /**
     * 将响应中的结果数组，转为为T[]
     * @param responseR
     * @return
     */
    private T[] extractResult( TrpcResponse responseR) {
        TrpcMultipleResponse response = (TrpcMultipleResponse)responseR;
        Object o = Array.newInstance(response.getReturnType(), response.getDataArr().length);
        T[] ret = (T[])o;
        for (int i = 0; i < ret.length; i++) {
            ret[i] = (T) response.getDataArr()[i];
        }
        return ret;
    }
}
