package tong.trpc.core.zipkin;

import brave.rpc.RpcServerResponse;
import tong.trpc.core.domain.response.TrpcResponse;

/**
 * @Author tong-exists
 * @Create 2023/3/14 21:10
 * @Version 1.0
 */
public class TrpcZipkinServerResponse extends RpcServerResponse {

    private final TrpcZipkinServerRequest requestWrapper;
    private final Throwable error;
    private TrpcResponse trpcResponse;


    public TrpcZipkinServerResponse(TrpcZipkinServerRequest requestWrapper, TrpcResponse response, Throwable error) {
        this.requestWrapper = requestWrapper;
        this.trpcResponse = response;
        this.error = error;
    }

    @Override
    public String errorCode() {
        return error == null? null: ""+ this.trpcResponse.getCode() + ":" + this.trpcResponse.getMsg();
    }

    @Override
    public Throwable error() {
        return error;
    }

    @Override
    public Object unwrap() {
        return "TrpcZipkinServerResponse unwrap";
    }
}
