package common.api.service;

import com.example.tutorial.protos.Person;

public interface HelloService {

    void helloWithoutResult(String name);

    String helloWithResult(String name);

    Person helloPerson(Person p);

}
