package tong.trpc.core.domain.request;

import tong.trpc.core.domain.response.TrpcResponse;

import java.util.HashMap;

public interface TrpcRequest  {
    TrpcResponse newResponse();

    public long getRequestId() ;

    public void setRequestId(long requestId) ;

    public String getServiceInstanceName() ;

    public void setServiceInstanceName(String serviceInstanceName) ;

    public HashMap<String, Object> getAttributes() ;

    public void setAttributes(HashMap<String, Object> attributes) ;

    public int getRequestType() ;

    public void setRequestType(int requestType) ;

    public String getClassName() ;

    public void setClassName(String className) ;

    public String getMethodName() ;

    public void setMethodName(String methodName) ;

    public Object[] getParams() ;

    public void setParams(Object[] params) ;

    public Class<?>[] getParamsTypes() ;

    public void setParamsTypes(Class<?>[] paramsTypes) ;

    public Class<?> getReturnType() ;

    public void setReturnType(Class<?> returnType) ;
}
