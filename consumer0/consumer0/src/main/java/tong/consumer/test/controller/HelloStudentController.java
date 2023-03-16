package tong.consumer.test.controller;

import common.api.service.TrpcEchoService;
import common.api.service.TrpcHelloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tong.consumer.test.model.Student;

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


}
