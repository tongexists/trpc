package tong.trpc.core.zipkin;

import brave.rpc.RpcServerRequest;
import tong.trpc.core.domain.TrpcRequest;

/**
 * @Author tong-exists
 * @Create 2023/3/14 21:10
 * @Version 1.0
 */
public class TrpcZipkinServerRequest extends RpcServerRequest {

    private TrpcRequest trpcRequest;

    public TrpcZipkinServerRequest(TrpcRequest trpcRequest) {
        this.trpcRequest = trpcRequest;
    }

    @Override
    public String method() {
        return this.trpcRequest.getMethodName();
    }

    @Override
    public String service() {
        return this.trpcRequest.getClassName();
    }

    @Override
    public Object unwrap() {
        return "TrpcZipkinServerRequest unwrap";
    }

    @Override
    protected String propagationField(String keyName) {
        return this.trpcRequest.getTraceMap().get(keyName);
    }
}
