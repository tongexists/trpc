# 自定义请求处理
要点：
1. 实现TrpcRequest和TrpcResponse，自定义请求和响应
2. 继续使用TrpcInvocation或自定义Invocation
3. 实现TrpcClientFilter和TrpcResponseFIlter，添加到过滤器链，处理自定义的请求和响应。

# 自定义请求和响应
实现TrpcRequest和TrpcResponse，可参考tong.trpc.core.domain.request.TrpcMultipleRequest和tong.trpc.core.domain.response.TrpcMultipleResponse

newResponse必须要重写，返回对应响应的一个实例。重写getParams，保证分布式追踪时记录的入参是我们想要的。
```java
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)

public class TrpcMultipleRequest extends TrpcRequestImpl {
    /**
     * 入参数组
     */
    private Object[][] paramsArr;

    @Override
    public TrpcResponse newResponse() {
        return new TrpcMultipleResponse();
    }

    @Override
    public Object[] getParams() {
        return Arrays.stream(paramsArr).flatMap(arr -> Arrays.stream(arr)).toArray();
    }


}
```
重写getData，保证分布式追踪时记录的结果是我们想要的。
```java
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TrpcMultipleResponse extends TrpcResponseImpl{
    /**
     * 返回的结果数组
     */
    private Object[] dataArr;

    @Override
    public Object getData() {
        return this.dataArr;
    }
}
```

# Invocation
## 继续使用TrpcInvocation
当我们仅是要更换TrpcRequest的实现，而不用对协议进行操作时，推荐继续使用TrpcInvocation

TrpcInvocation的默认TrpcRequest的实现是tong.trpc.core.domain.request.TrpcRequestImpl，因此我们需要更换为我们要的，请调用TrpcInvocation.configInvocation
```java
productService.getProduct(...).configInvocation(new Consumer<TrpcInvocation<Product>>() {
            @Override
            public void accept(TrpcInvocation<Product> productTrpcInvocation) {
                productTrpcInvocation.setRequest(...);
            }
}).sync();
```

## 自定义Invocation
当我们需要更加复杂逻辑的Invocation，例如特定的输入，我们可以自定义Invocation
### 1. 自定义Invocation
可以参考tong.trpc.core.invocation.TrpcMultipleInvocation。TrpcMultipleInvocation采用装饰器模式对tong.trpc.core.invocation.TrpcInvocation进行操作。但是TrpcInvocation最重要的是里面的数据，包含了调用的接口名、方法名、入参类型等，发起请求的过程并不重要，我们可以在自定义的Invocation中自己写发起请求的逻辑。

发起请求的逻辑可以参考tong.trpc.core.invocation.TrpcInvocation.sendRequest。其实很简单，就是调用tong.trpc.core.util.TrpcInvocationUtils.invokeByProtocol。如果不想操作Protocol，也可以调用tong.trpc.core.util.TrpcInvocationUtils.invoke

### 2. 调用TrpcInvocation.transfer转换为自定义的Invocation
```java
        productService.getProduct(order).transfer(new Function<TrpcInvocation<Product>, XxInvocation>() {
            @Override
            public XxInvocation apply(TrpcInvocation<Product> productTrpcInvocation) {
                return null;
            }
        });
```

# 自定义过滤器
实现TrpcClientFilter和TrpcResponseFIlter

## 1. TrpcClientFilter
```java
public class XxFilter implements TrpcClientFilter {

    @Override
    public void doFilter(TrpcRequest request, CompletableFuture<TrpcResponse> future, TrpcClientFilterChain chain) {
        // 不是要处理的请求，放行
        if (!(request instanceof MyXxRequest)) {
            chain.doFilter(request, future);
        }
        //发起请求前处理，比如对request操作、添加响应完成要回调做的事...
        future.whenComplete(new BiConsumer<TrpcResponse, Throwable>() {
            @Override
            public void accept(TrpcResponse trpcResponse, Throwable throwable) {

            }
        });
        chain.doFilter(request, future);
        //future.get()获取结果
        //发起请求后处理...
    }

    @Override
    public boolean isEnable() {
        return true;
    }
    /**
     * 顺序，决定了在过滤器链中的位置，按字典升序，请使用数字
     * 例如 0, 1, 10, 11, 2
     * @return 顺序
     */
    @Override
    public String order() {
        
    }
}
```

### 1.1过滤器链中的顺序
默认的过滤器顺序见tong.trpc.core.filter.client.TrpcClientFiltersOrder

比如自定义的过滤器应该在TrpcClientExceptionHandlerFilter和TrpcClientTracingInterceptor之间，那么自定义的过滤器的order方法返回是01或者02或者022，就是字典序要在0和1之间的

需要关注的是TrpcSendRequestFilter，他是具体发送请求的过滤器。
```java
    TrpcClientExceptionHandlerFilter("0", "TrpcClientExceptionHandlerFilter"),
    TrpcClientTracingInterceptor("1", "TrpcClientTracingInterceptor"),
    TrpcSendRequestFilter("2", "TrpcSendRequestFilter");
```
### 1.2添加到过滤器链
调用tong.trpc.core.filter.client.TrpcClientFilters.add()方法

## 2.TrpcServerFilter
可以参考tong.trpc.core.filter.server.TrpcDealTrpcRequestImplFilter，他是处理默认请求的过滤器
```java
public class TrpcServerXxFilter implements TrpcServerFilter {

    @Override
    public void doFilter(TrpcRequest request, TrpcResponse response, TrpcServerFilterChain chain) {
        // 不是要处理的request，放行
        if (!(request instanceof MyxxRequest)) {
            chain.doFilter(request, future);
        }
        // 处理逻辑...
        // 根据情况决定是否让下一个过滤器处理chain.doFilter(request, response);
        
    }

    @Override
    public boolean isEnable() {
        return true;
    }

    @Override
    public String order() {
        return TrpcServerFiltersOrder.TrpcServerExceptionHandlerFilter.getOrder();
    }
}
```

### 2.1过滤器的顺序
跟TrpcClientFilter一样的，默认的见tong.trpc.core.filter.client.TrpcClientFiltersOrder

### 2.2 添加过滤器到过滤器链
tong.trpc.core.filter.server.TrpcServerFilters.add()
