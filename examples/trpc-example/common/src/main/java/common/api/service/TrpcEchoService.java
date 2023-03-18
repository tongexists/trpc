package common.api.service;

import tong.consumer.test.model.Student;
import tong.trpc.core.TrpcInvocation;
import tong.trpc.core.annotation.TrpcService;

/**
 * @Author tong-exists
 * @Create 2023/3/15 9:27
 * @Version 1.0
 */
@TrpcService(serviceInstanceName = "echo-service")
public interface TrpcEchoService {

    TrpcInvocation<String> echoStudent(int a, Student p);

}
