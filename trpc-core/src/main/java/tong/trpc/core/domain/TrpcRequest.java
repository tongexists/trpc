package tong.trpc.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrpcRequest  implements Serializable {

    private String className; // 类名

    private String methodName; //请求目标方法名

    private Object[] params; // 请求参数

    private Class<?>[] paramsTypes; // 参数类型

    private HashMap<String, String> traceMap = new HashMap<>();

    private long requestId; // 请求ID 8个字节

    /**
     * 注册中心服务实例的名称
     */
    private String serviceInstanceName;

    private HashMap<String, Object> attributes = new HashMap<>();

}
