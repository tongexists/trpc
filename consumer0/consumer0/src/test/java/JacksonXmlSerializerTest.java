import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import tong.consumer.test.model.Student;
import tong.trpc.core.domain.TrpcRequest;
import tong.trpc.core.util.JacksonXmlSerializerUtil;

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
public class JacksonXmlSerializerTest {

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
        request.setParamsTypes(new String[]{Student.class.getName()});

        String s = JacksonXmlSerializerUtil.objToXml(request);
        log.info(s);
        TrpcRequest request1 = JacksonXmlSerializerUtil.xmlToObj(s, TrpcRequest.class);
        log.info(String.valueOf(request));
        Class<?> stuClass = Class.forName(request1.getParamsTypes()[0]);
        Object o = JacksonXmlSerializerUtil.xmlToObj(JacksonXmlSerializerUtil.objToXml(request1.getParams()[0]), stuClass);

        log.info(String.valueOf(o));
        log.info(String.valueOf(request1));


    }

}