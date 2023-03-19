package tong.trpc.core.domain.request;

import lombok.Data;

import java.util.HashMap;

/**
 * 抽象请求
 * @Author tong-exists
 * @Create 2023/3/17 13:46
 * @Version 1.0
 */
@Data
public abstract class AbstractTrpcRequest implements TrpcRequest{
    private long requestId; // 请求ID 8个字节

    /**
     * 注册中心服务实例的名称
     */
    private String serviceInstanceName;
    /**
     * 属性
     */
    private HashMap<String, Object> attributes = new HashMap<>();
    /**
     * 见请求类型TrpcRequestType
     */
    private int requestType;


}
