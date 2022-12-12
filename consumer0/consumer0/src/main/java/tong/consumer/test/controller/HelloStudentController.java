package tong.consumer.test.controller;

import common.api.service.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tong.consumer.test.model.Student;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

    @GetMapping("/helloStudent")
    public void helloStudent() {
        Student student = new Student();
        student.setName("name1");
        student.setAge(10);
        student.setScore(10.2);
        student.setLikes(new String[]{"a", "b"});
        Student name2 = new Student();
        name2.setName("name2");
        Student name3 = new Student();
        name3.setName("name3");

        student.setFriends(Arrays.asList(name2, name3));
        Map<String, String> sm = new HashMap<>();
        sm.put("A", "1");
        sm.put("B", "2");
        student.setScoreMap(sm);
        log.info("发出: {}", student);
        Student result = helloService.helloStudent(student).sync();
        log.info("响应: {}", result);
    }

}
