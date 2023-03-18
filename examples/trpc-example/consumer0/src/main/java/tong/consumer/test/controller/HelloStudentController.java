package tong.consumer.test.controller;

import common.api.service.TrpcEchoService;
import common.api.service.TrpcHelloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tong.consumer.test.model.Student;
import tong.trpc.core.TrpcInvocation;
import tong.trpc.core.util.FastjsonSerializerUtil;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

/**
 * @Author tong-exists
 * @Create 2022/12/12 10:34
 * @Version 1.0
 */
@Slf4j
@RestController
public class HelloStudentController {

    @Autowired
    private TrpcHelloService helloService;

    @Autowired
    private TrpcEchoService echoService;

    @PostMapping("/helloStudent")
    public void helloStudent(@RequestBody Student student) {
        Student result = helloService.helloStudent(student).sync();
        log.info("响应: {}", result);
    }

    @PostMapping("/depth2")
    public String depth2(@RequestBody Student student) {
        String student1 = helloService.callEchoService(student.getAge(), student).sync();
        log.info(student1);
        return student1;
    }

    @PostMapping("/width2")
    public String width2(@RequestBody Student student) {
        Student student1 = helloService.helloStudent(student).sync();
        String s = echoService.echoStudent(student.getAge(), student1).sync();
        log.info("helloStudent:{}", student1.toString());
        log.info("echoStudent:{}", s);
        return s;
    }

    @RequestMapping(method = RequestMethod.POST,path = "/width-depth2")
    public String widthDepth2(@RequestBody Student student) {
        String student1 = helloService.callEchoService(student.getAge(), student).sync();
        String s = echoService.echoStudent(student.getAge(), student).sync();
        log.info("callEchoService:{}", student1.toString());
        log.info("echoStudent:{}", s);
        return s;
    }

    @RequestMapping(method = RequestMethod.POST,path = "/multipleWidthDepth2Sync")
    public String multipleWidthDepth2Sync(@RequestBody Student student) {
//        String student1 = helloService.callEchoService(student.getAge(), student).sync();
        String[] s = echoService.echoStudent(student.getAge(), student).multipleCall(
                new Object[]{student.getAge(), student }, new Object[]{1, student}
        ).sync();
//        log.info("callEchoService:{}", student1.toString());
        log.info("echoStudent:{}", s);
        return FastjsonSerializerUtil.objectToJson(s);
    }

    @RequestMapping(method = RequestMethod.POST,path = "/multipleWidthDepth2Future")
    public String multipleWidthDepth2Future(@RequestBody Student student) {
//        String student1 = helloService.callEchoService(student.getAge(), student).sync();
        String[] s = new String[0];
        try {
            s = echoService.echoStudent(student.getAge(), student).multipleCall(
                    new Object[]{student.getAge(), student }, new Object[]{1, student}
            ).future().get();
//        log.info("callEchoService:{}", student1.toString());
            log.info("echoStudent:{}", s);
            return FastjsonSerializerUtil.objectToJson(s);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping(method = RequestMethod.POST,path = "/multipleWidthDepth2Callback")
    public String multipleWidthDepth2Callback(@RequestBody Student student) {
//        String student1 = helloService.callEchoService(student.getAge(), student).sync();
        final String[][] s = {new String[0]};
        echoService.echoStudent(student.getAge(), student).multipleCall(
                new Object[]{student.getAge(), student }, new Object[]{1, student}
        ).callback(new TrpcInvocation.CallBack<String[]>() {
            @Override
            public void onSuccess(String[] strings) {
                s[0] = strings;
                log.info(Arrays.toString(strings));
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
//        log.info("callEchoService:{}", student1.toString());
        log.info("echoStudent:{}", s[0]);
        return FastjsonSerializerUtil.objectToJson(s[0]);
    }

}
