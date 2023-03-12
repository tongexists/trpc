package tong.consumer.test.controller;

import common.api.service.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
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
    private HelloService helloService;

    @PostMapping("/helloStudent")
    public void helloStudent(@RequestBody Student student) {
        Student result = helloService.helloStudent(student).sync();
        log.info("响应: {}", result);
    }

}
