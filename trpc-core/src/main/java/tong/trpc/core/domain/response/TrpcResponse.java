package tong.trpc.core.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 抽象响应
 */
public interface TrpcResponse  {
    /**
     * 返回方法执行结果
     * @return 返回方法执行结果
     */
    public Object getData() ;

    public void setData(Object data) ;

    /**
     * 获取响应消息
     * @return 获取响应消息
     */
    public String getMsg() ;

    public void setMsg(String msg) ;

    /**
     * 获取响应码
     * @return 获取响应码
     */
    public int getCode() ;

    public void setCode(int code) ;

    /**
     * 获取返回类型
     * @return 获取返回类型
     */
    public Class<?> getReturnType() ;

    public void setReturnType(Class<?> returnType) ;

    /**
     * 获取请求id
     * @return 获取请求id
     */
    public long getRequestId() ;

    public void setRequestId(long requestId) ;

    /**
     * 获取响应类型
     * @return 获取响应类型
     */
    public int getResponseType() ;

    public void setResponseType(int responseType) ;

}
