package tong.trpc.core.domain;

import brave.propagation.TraceContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 存放请求id、请求和响应的对应关系
 */
public class TrpcRequestHolder {

    /*
  原子性 请求ID
   */
    public static final AtomicLong REQUEST_ID = new AtomicLong();

    /*
    保存请求ID和返回数据的关系
     */
    public static final Map<Long, TrpcFutureDecorator> REQUEST_MAP = new ConcurrentHashMap<>();

    /**
     * 请求id映射到TrpcTransportProtocol
     */
    public static final ConcurrentHashMap<Long, TrpcTransportProtocol> PROTOCOL_MAP = new ConcurrentHashMap<>();
}
