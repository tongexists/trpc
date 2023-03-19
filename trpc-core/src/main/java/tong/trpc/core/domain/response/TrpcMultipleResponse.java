package tong.trpc.core.domain.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 对应TrpcMultipleRequest
 * @Author tong-exists
 * @Create 2023/3/17 14:13
 * @Version 1.0
 */
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
