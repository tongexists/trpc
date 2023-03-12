import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import tong.consumer.test.model.Student;
import tong.trpc.core.domain.TrpcRequest;
import tong.trpc.core.util.FstSerializerUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author tong-exists
 * @Create 2022/12/12 14:31
 * @Version 1.0
 */
@Slf4j
public class FstSerializerTest {

    @Test
    public void test() throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        //student
        Student student = new Student();
        student.setName("name1");
        student.setAge(10);
        student.setScore(10.2);
        student.setLikes(new String[]{"a", "b"});
        Student name2 = new Student();
        name2.setName("name2");
        Student name3 = new Student();
        name3.setName("name3");
        student.setFriends(Arrays.asList(name2, name3));
        Map<String, String> sm = new HashMap<>();
        sm.put("A", "1");
        sm.put("B", "2");
        student.setScoreMap(sm);

        //TrpcRequest
        TrpcRequest request = new TrpcRequest();
        request.setClassName("setClassName");
        request.setMethodName("setMethodName");
        request.setParams(new Object[]{student});
//        request.setParamsTypes(new Class<?>[]{Student.class});
        request.setParamsTypes(new Class<?>[]{Student.class});

        byte[] serialize = FstSerializerUtil.serialize(request);
        TrpcRequest deserialize = FstSerializerUtil.deserialize(serialize);
        System.out.println(deserialize);
        Student stu = (Student) deserialize.getParams()[0];
        log.info(String.valueOf(stu));

    }

}
