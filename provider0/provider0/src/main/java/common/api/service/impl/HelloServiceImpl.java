package common.api.service.impl;


import com.example.tutorial.protos.Person;
import common.api.service.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


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
    public Person helloPerson(Person p) {
        log.info("收到: {}", p);
        Person john =
                Person.newBuilder()
                        .setId(1234)
                        .setName(p.getName())
                        .setEmail("jdoe@example.com")
                        .addPhones(
                                Person.PhoneNumber.newBuilder()
                                        .setNumber("555-4321")
                                        .setType(Person.PhoneType.HOME))
                        .build();
        return john;
    }


}
