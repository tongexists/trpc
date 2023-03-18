package common.api.service.impl;


import common.api.service.HelloService;
import common.api.service.TrpcEchoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tong.consumer.test.model.Student;


@Slf4j
@Service
public class HelloServiceImpl implements HelloService {

    @Autowired
    private TrpcEchoService echoService;

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
        return p;
    }

    @Override
    public String callEchoService(int a, Student p) {
        return echoService.echoStudent(a, p).sync();
    }


}
