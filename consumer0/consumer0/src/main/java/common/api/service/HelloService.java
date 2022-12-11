package common.api.service;

import com.example.tutorial.protos.Person;
import tong.trpc.core.TrpcInvocation;
import tong.trpc.core.annotation.TrpcService;
import tong.trpc.core.io.serialize.TrpcSerialType;


@TrpcService(serviceInstanceName = "helloService", serialType = TrpcSerialType.TrpcJdkSerializer)
public interface HelloService {

    TrpcInvocation<Void> helloWithoutResult(String name);

    TrpcInvocation<String> helloWithResult(String name);
    TrpcInvocation<Person> helloPerson(Person p);
}
