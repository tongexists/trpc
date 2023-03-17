import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import tong.trpc.core.domain.request.TrpcRequest;
import tong.trpc.core.util.KryoSerializerUtil;

import java.io.IOException;
import java.lang.reflect.*;

/**
 * @Author tong-exists
 * @Create 2023/3/17 11:55
 * @Version 1.0
 *
 */
@Slf4j
public class TestTypeParam {
    @Test
    public void test() throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException {
        MultipleRequest multipleRequest = new MultipleRequest();
        multipleRequest.setHello("hello");
        multipleRequest.setClassName("dfsdfs");
        byte[] serialize = KryoSerializerUtil.serialize(multipleRequest);
        TrpcRequest deserialize = KryoSerializerUtil.deserialize(serialize, MultipleRequest.class);
        log.info(deserialize.toString());
    }


}
