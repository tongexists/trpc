package common.api.service;

import tong.consumer.test.model.Student;


public interface HelloService {

    void helloWithoutResult(String name);

    String helloWithResult(String name);


    Student helloStudent(Student p);

    String callEchoService(int a, Student p);

}
