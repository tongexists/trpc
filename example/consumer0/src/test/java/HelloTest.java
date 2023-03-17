import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

/**
 * @Author tong-exists
 * @Create 2022/12/13 19:03
 * @Version 1.0
 */
@Slf4j
public class HelloTest {
    @Test
    public void test() throws NoSuchMethodException, ClassNotFoundException {
//        Method method = HelloService.class.getDeclaredMethod("helloStudent", Student.class);
        Method method = HelloService.class.getDeclaredMethod("zzz");
        log.info(method.getReturnType().getName());
        log.info(method.getReturnType().getSimpleName());
        log.info(method.getReturnType().getTypeName());
        log.info(method.getReturnType().getCanonicalName());
    }

    @Test
    public void test2() throws NoSuchMethodException, ClassNotFoundException {
        String a = "22";
        Object b = a;
        String c = (String) b;
        System.out.println(c);
    }
}
