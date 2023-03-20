package tong.trpc.core.domain.request;

import tong.trpc.core.domain.response.TrpcResponse;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 请求接口
 */
public interface TrpcRequest extends Serializable {
    /**
     * 创建一个对应的TrpcResponse实例
     * @return 一个对应的TrpcResponse实例
     */
    TrpcResponse newResponse();

    /**
     * 请求id
     * @return 请求id
     */
    public long getRequestId() ;

    /**
     * 设置请求id
     * @param requestId 请求id
     */

    public void setRequestId(long requestId) ;

    /**
     * 获取服务名
     * @return 服务名
     */
    public String getServiceInstanceName() ;

    /**
     * 设置服务名
     * @param serviceInstanceName 服务名
     */
    public void setServiceInstanceName(String serviceInstanceName) ;

    /**
     * 获取属性
     * @return
     */
    public HashMap<String, Object> getAttributes() ;

    /**
     * 设置属性
     * @param attributes 属性
     */
    public void setAttributes(HashMap<String, Object> attributes) ;

    /**
     * 获取请求类型，TrpcRequestType
     * @return 请求类型代码
     */
    public int getRequestType() ;

    public void setRequestType(int requestType) ;

    /**
     * 获取调用的接口类名
     * @return 获取调用的接口类名
     */
    public String getClassName() ;

    public void setClassName(String className) ;

    /**
     * 调用的方法名
     * @return 调用的方法名
     */
    public String getMethodName() ;

    public void setMethodName(String methodName) ;

    /**
     * 获取入参
     * @return 获取入参
     */
    public Object[] getParams() ;

    public void setParams(Object[] params) ;

    /**
     * 获取要调用的方法的入参类型
     * @return 获取要调用的方法的入参类型
     */
    public Class<?>[] getParamsTypes() ;

    public void setParamsTypes(Class<?>[] paramsTypes) ;

    /**
     * 获取要调用的方法的返回类型
     * @return 获取要调用的方法的返回类型
     */
    public Class<?> getReturnType() ;

    public void setReturnType(Class<?> returnType) ;
}
