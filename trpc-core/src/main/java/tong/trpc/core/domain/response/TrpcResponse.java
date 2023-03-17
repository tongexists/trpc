package tong.trpc.core.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

public interface TrpcResponse  {
    public Object getData() ;

    public void setData(Object data) ;

    public String getMsg() ;

    public void setMsg(String msg) ;

    public int getCode() ;

    public void setCode(int code) ;

    public Class<?> getReturnType() ;

    public void setReturnType(Class<?> returnType) ;

    public long getRequestId() ;

    public void setRequestId(long requestId) ;


    public int getResponseType() ;

    public void setResponseType(int responseType) ;

}
