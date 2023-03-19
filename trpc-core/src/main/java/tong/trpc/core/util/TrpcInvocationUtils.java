package tong.trpc.core.util;

import tong.trpc.core.invocation.TrpcInvocation;

/**
 * 调用工具类
 * @Author tong-exists
 * @Create 2023/3/17 10:14
 * @Version 1.0
 */
public class TrpcInvocationUtils {

    /**
     * 同步调用多个TrpcInvocation
     * @param invocations
     * @return
     */
    public static Object[] allSync(TrpcInvocation ...invocations) {
        Object[] ret = new Object[invocations.length];
        for (int i = 0; i < invocations.length; i++) {
            ret[i] = invocations[i].sync();
        }
        return ret;
    }




}
