package tong.trpc.examples.order_example.common.domain;

import lombok.Data;

/**
 * @Author tong-exists
 * @Create 2023/3/18 10:06
 * @Version 1.0
 */
@Data
public class Product {

    private Long productId;

    private String name;

    private String desc;

    private Integer price;

}
