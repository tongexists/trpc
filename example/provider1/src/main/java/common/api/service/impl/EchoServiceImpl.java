package common.api.service.impl;

import common.api.service.EchoService;
import org.springframework.stereotype.Service;
import tong.consumer.test.model.Student;

/**
 * @Author tong-exists
 * @Create 2023/3/15 9:28
 * @Version 1.0
 */
@Service
public class EchoServiceImpl implements EchoService {
    @Override
    public String echoStudent(int a, Student p) {
        return p.toString() + ":" + a;
    }
}
