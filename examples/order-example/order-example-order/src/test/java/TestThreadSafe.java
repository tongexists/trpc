import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.Temperature;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import tong.trpc.examples.order_example.common.domain.Order;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author tong-exists
 * @Create 2023/3/18 16:25
 * @Version 1.0
 */
@Slf4j
public class TestThreadSafe {

    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            30,40, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10000));

    @Test
    public void test() {
        for (int i = 0; i < 10000; i++) {
            threadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    Random random = new Random(System.currentTimeMillis() + new Random(System.currentTimeMillis()).nextLong());
                    RestTemplate restTemplate = new RestTemplate();
                    HashMap<String, Object> paramMap = new HashMap<String, Object>();
                    Long productId = random.nextLong();
                    paramMap.put("productId", productId);
                    paramMap.put("count", 2);
                    MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
                    headers.put("Content-Type", Arrays.stream(new String[]{"application/json"}).collect(Collectors.toList()));
                    HttpEntity<HashMap<String, Object>> entity = new HttpEntity<>(paramMap, headers);
                    String url = random.nextInt(2) == 0? "http://localhost:8004/order/createOrder": "http://localhost:8004/order/createOrderDepth";
                    ResponseEntity<Order> responseEntity = restTemplate.postForEntity(
                            url, entity, Order.class);
                    Order order = responseEntity.getBody();
                    Assertions.assertEquals(productId, order.getProductId());
                    Assertions.assertEquals(String.format("Name[%s]", productId), order.getDesc());
                    log.info("ok");
                }
            });
        }
        try {
            Thread.sleep(60 * 1000 * 60);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}
