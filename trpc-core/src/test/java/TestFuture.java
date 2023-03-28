import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

/**
 * @Author tong-exists
 * @Create 2023/3/16 14:14
 * @Version 1.0
 */
@Slf4j
public class TestFuture {


    @Test
    public void test() {
        CompletableFuture<Integer> completableFuture = new CompletableFuture<>();
        completableFuture.whenComplete(new BiConsumer<Integer, Throwable>() {
            @Override
            public void accept(Integer integer, Throwable throwable) {
                log.info("完成后回调{}：{}",Thread.currentThread().getName(), integer);
            }
        });
        completableFuture.whenComplete(new BiConsumer<Integer, Throwable>() {
            @Override
            public void accept(Integer integer, Throwable throwable) {
                log.info("完成后回调2222222{}：{}",Thread.currentThread().getName(), integer);
            }
        });


        log.info("测试主线程{}", Thread.currentThread().getName());
        new Thread(() -> {
            completableFuture.complete(1);
        }).start();

        try {
            Thread.sleep(1000);
            Integer integer = completableFuture.get();
            Integer integer2 = completableFuture.get();
            log.info(integer + "");
            log.info(integer2+ "");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testF() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> a = new CompletableFuture<>();
        a.whenComplete(new BiConsumer<Integer, Throwable>() {
            @Override
            public void accept(Integer integer, Throwable throwable) {
                log.info("0000000000000");
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                a.complete(1);
            }
        }).start();
        a.whenComplete(new BiConsumer<Integer, Throwable>() {
            @Override
            public void accept(Integer integer, Throwable throwable) {
                log.info("aaaaaaaaaaa");
            }
        });
//        Integer integer = a.get();
//        log.info("i:-----------------{}", integer);
        a.whenComplete(new BiConsumer<Integer, Throwable>() {
            @Override
            public void accept(Integer integer, Throwable throwable) {
                log.info("bbbbbbbbbbbbbbb");
            }
        });

    }
}
