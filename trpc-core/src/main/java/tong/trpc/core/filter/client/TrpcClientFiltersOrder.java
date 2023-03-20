package tong.trpc.core.filter.client;

import lombok.Getter;

/**
 * 客户端过滤器的顺序
 * @Author tong-exists
 * @Create 2023/3/19 16:52
 * @Version 1.0
 */
@Getter
public enum TrpcClientFiltersOrder {
    /**
     * TrpcClientExceptionHandlerFilter
     */
    TrpcClientExceptionHandlerFilter("0", "TrpcClientExceptionHandlerFilter"),
    TrpcClientTracingInterceptor("1", "TrpcClientTracingInterceptor"),
    TrpcSendRequestFilter("2", "TrpcSendRequestFilter");

    /**
     * 顺序，决定了在过滤器链中的位置，按字典升序，请使用数字
     * 例如 0, 1, 10, 11, 2
     */
    private String order;
    /**
     * 过滤器名
     */
    private String filterName;

    private TrpcClientFiltersOrder(String order, String filterName) {
        this.order = order;
        this.filterName = filterName;
    }
}
