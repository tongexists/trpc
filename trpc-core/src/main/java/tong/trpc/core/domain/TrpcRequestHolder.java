package tong.trpc.core.domain;

import brave.propagation.TraceContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class TrpcRequestHolder {

    /*
  原子性 请求ID
   */
    public static final AtomicLong REQUEST_ID = new AtomicLong();

    /*
    保存请求ID和返回数据的关系
     */
    public static final Map<Long, TrpcFutureDecorator> REQUEST_MAP = new ConcurrentHashMap<>();

}
