package tong.consumer.test.controller;


import common.api.service.TrpcHelloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tong.trpc.core.TrpcInvocation;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


@RestController
@Slf4j
public class HelloController {

    @Autowired
    private TrpcHelloService helloService;

    @GetMapping("/helloWithoutResult")
    public void helloWithoutResult(@RequestParam("name") String name) {
        helloService.helloWithoutResult(name).sync();
    }

    @GetMapping("/helloWithResult")
    public void helloWithResult(@RequestParam("name") String name) {
        log.info(helloService.helloWithResult(name).sync());
    }

    @GetMapping("/future")
    public void future(@RequestParam("name") String name) {
        CompletableFuture<String> future = helloService.helloWithResult(name).future();
        log.info("aaaaaaaaaaaa");
        try {
            log.info( future.get());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

    }

    @GetMapping("/CallBack")
    public void CallBack(@RequestParam("name") String name) {
        log.info("bbbbbbbbbbb");
        helloService.helloWithResult(name).callback(new TrpcInvocation.CallBack<String>() {
            @Override
            public void onSuccess(String s) {
                log.info(s);
            }

            @Override
            public void onFailure(Exception e) {
                log.info(e.toString());
            }
        });
        log.info("cccccccccccccc");
    }


}
