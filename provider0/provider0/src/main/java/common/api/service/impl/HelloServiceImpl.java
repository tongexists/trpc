package common.api.service.impl;


import common.api.service.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tong.consumer.test.model.Student;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Service
public class HelloServiceImpl implements HelloService {

    @Override
    public void helloWithoutResult(String name) {
        log.info(name);
    }

    @Override
    public String helloWithResult(String name) {
        log.info(name);
        return "回复：我是服务端，" + name + "，你好啊";
    }



    @Override
    public Student helloStudent(Student p) {
        log.info("收到: {}", p);
        Student student = new Student();
        student.setName("name1111111111111111111111");
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
        log.info("返回响应: {}", student);
        return student;
    }


}
