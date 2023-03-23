package tong.trpc.examples.order_example.common.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author tong-exists
 * @Create 2023/3/18 10:11
 * @Version 1.0
 */
@Data
public class Storage implements Serializable {

    private Long storageId;

    private Long productId;

    private Integer stock;

}
