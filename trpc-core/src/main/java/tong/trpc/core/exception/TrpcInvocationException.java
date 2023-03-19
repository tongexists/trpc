package tong.trpc.core.exception;

/**
 * 调用异常
 * @Author tong-exists
 * @Create 2023/3/18 13:01
 * @Version 1.0
 */
public class TrpcInvocationException extends Exception {

    public TrpcInvocationException(String msg, Exception e) {
        super(msg, e);
    }
}
