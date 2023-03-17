package tong.trpc.core.zipkin;

import brave.rpc.RpcClientRequest;
import tong.trpc.core.domain.request.TrpcRequest;

/**
 * @Author tong-exists
 * @Create 2023/3/14 21:10
 * @Version 1.0
 */
public class TrpcZipkinClientRequest extends RpcClientRequest {

    private TrpcRequest trpcRequest;

    public TrpcZipkinClientRequest(TrpcRequest trpcRequest) {
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
        return "TrpcZipkinClientRequest unwrap";
    }

    @Override
    protected void propagationField(String keyName, String value) {
        this.trpcRequest.getAttributes().put(keyName, value);
    }
}
