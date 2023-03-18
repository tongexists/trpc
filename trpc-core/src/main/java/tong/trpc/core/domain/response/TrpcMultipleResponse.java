package tong.trpc.core.domain.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @Author tong-exists
 * @Create 2023/3/17 14:13
 * @Version 1.0
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TrpcMultipleResponse extends TrpcResponseImpl{

    private Object[] dataArr;

    @Override
    public Object getData() {
        return this.dataArr;
    }
}
