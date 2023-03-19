package tong.trpc.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应码枚举
 */
@Getter
@AllArgsConstructor
public enum TrpcResponseCode {
    /**
     * 成功
     */
    SUCCESS(1, "success"),
    /**
     *  错误
     */
    ERROR(2, "error");
    /**
     * 代码
     */
    private int code;
    /**
     * 信息
     */
    private String msg;

}
