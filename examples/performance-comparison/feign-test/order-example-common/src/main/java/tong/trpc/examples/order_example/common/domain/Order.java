package tong.trpc.examples.order_example.common.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author tong-exists
 * @Create 2023/3/18 10:12
 * @Version 1.0
 */
@Data
public class Order implements Serializable {

    private Long orderId;
    private Long productId;
    private Integer count;
    private Long totalPrice;
    private String desc;

}
