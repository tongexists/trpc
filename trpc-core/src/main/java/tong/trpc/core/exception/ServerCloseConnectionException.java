package tong.trpc.core.exception;

/**
 * @Author tong-exists
 * @Create 2023/3/12 17:01
 * @Version 1.0
 */
public class ServerCloseConnectionException extends Exception {

    public ServerCloseConnectionException(String msg) {
        super(msg);
    }

}
