package tong.trpc.examples.order_example.common.domain;

import lombok.Data;

/**
 * @Author tong-exists
 * @Create 2023/3/18 10:11
 * @Version 1.0
 */
@Data
public class Storage {

    private Long storageId;

    private Long productId;

    private Integer stock;

}
