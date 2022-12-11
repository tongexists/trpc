package tong.consumer.test.controller;

import com.example.tutorial.protos.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import common.api.service.HelloService;

/**
 * @Author tong-exists
 * @Create 2022/12/11 16:19
 * @Version 1.0
 */
@RestController
@Slf4j
public class HelloPersonController {

    @Autowired
    private HelloService helloService;

    @GetMapping("/syncHelloPerson")
    public void syncHelloPerson(@RequestParam("i") Integer i) {
        Person john =
                Person.newBuilder()
                        .setId(1234)
                        .setName("John Doe" + i)
                        .setEmail("jdoe@example.com")
                        .addPhones(
                                Person.PhoneNumber.newBuilder()
                                        .setNumber("555-4321")
                                        .setType(Person.PhoneType.HOME))
                        .build();
        Person p = helloService.helloPerson(john).sync();
        log.info("sync: {}",p);
    }


}
