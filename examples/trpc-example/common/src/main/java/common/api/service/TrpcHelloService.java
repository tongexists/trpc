package common.api.service;

import tong.consumer.test.model.Student;
import tong.trpc.core.TrpcInvocation;
import tong.trpc.core.annotation.TrpcService;
import tong.trpc.core.io.serialize.TrpcSerialType;

import java.util.List;


@TrpcService(serviceInstanceName = "hello-service", serialType = TrpcSerialType.TrpcFastjsonSerializer)
public interface TrpcHelloService {

    TrpcInvocation<Void> helloWithoutResult(String name);

    TrpcInvocation<String> helloWithResult(String name);

    TrpcInvocation<Student> helloStudent(Student p);

    TrpcInvocation<List<Student>> zzz();

    TrpcInvocation<String> callEchoService(int a, Student p);

}
