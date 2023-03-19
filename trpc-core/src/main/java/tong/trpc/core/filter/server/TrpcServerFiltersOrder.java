package tong.trpc.core.filter.server;

import lombok.Getter;

/**
 * 过滤器顺序
 * @Author tong-exists
 * @Create 2023/3/19 17:04
 * @Version 1.0
 */
@Getter
public enum TrpcServerFiltersOrder {
    /**
     * TrpcDealTrpcMultipleRequestFilter
     */
    TrpcServerExceptionHandlerFilter("0", "TrpcServerExceptionHandlerFilter"),
    TrpcServerTracingInterceptor("1", "TrpcServerTracingInterceptor"),
    TrpcDealTrpcMultipleRequestFilter("2", "TrpcDealTrpcMultipleRequestFilter"),
    TrpcDealTrpcRequestImplFilter("3", "TrpcDealTrpcRequestImplFilter");

    /**
     * 顺序，决定了在过滤器链中的位置，按字典升序，请使用数字
     * 例如 0, 1, 10, 11, 2
     */
    private String order;
    /**
     * 过滤器名
     */
    private String filterName;

    private TrpcServerFiltersOrder(String order, String filterName) {
        this.order = order;
        this.filterName = filterName;
    }
}
