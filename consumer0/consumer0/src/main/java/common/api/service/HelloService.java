package common.api.service;

import tong.consumer.test.model.Student;
import tong.trpc.core.TrpcInvocation;
import tong.trpc.core.annotation.TrpcService;
import tong.trpc.core.io.serialize.TrpcSerialType;

import java.util.List;


@TrpcService(serviceInstanceName = "helloService", serialType = TrpcSerialType.TrpcGsonSerializer)
public interface HelloService {

    TrpcInvocation<Void> helloWithoutResult(String name);

    TrpcInvocation<String> helloWithResult(String name);

    TrpcInvocation<Student> helloStudent(Student p);

    TrpcInvocation<List<Student>> zzz();
}
