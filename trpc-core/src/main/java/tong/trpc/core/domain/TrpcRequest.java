package tong.trpc.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrpcRequest implements Serializable {

    private String className; // 类名

    private String methodName; //请求目标方法名

    private Object[] params; // 请求参数

    private String[] paramsTypes; // 参数类型

}
