package tong.trpc.core.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import tong.trpc.core.domain.request.TrpcRequest;
import tong.trpc.core.domain.response.TrpcResponse;
import tong.trpc.core.domain.TrpcTransportProtocol;
import tong.trpc.core.domain.TrpcTransportProtocolHeader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Kryo序列化工具
 * @Author tong-exists
 * @Create 2022/12/12 16:41
 * @Version 1.0
 */
public class KryoSerializerUtil {

    private static ThreadLocal<Kryo> kryoThreadLocal = new ThreadLocal() {
        @Override
        protected Kryo initialValue() {
            Kryo kryo = new Kryo();
            kryo.register(TrpcRequest.class);
            kryo.register(TrpcResponse.class);
            kryo.register(TrpcTransportProtocol.class);
            kryo.register(TrpcTransportProtocolHeader.class);
            kryo.setReferences(true);
            kryo.setRegistrationRequired(false);
            return kryo;
        }
    };

    private static final int OUTPUT_BUFFER_SIZE = 1024 * 1024 * 6;

    private static ThreadLocal<Output> outputThreadLocal = new ThreadLocal() {
        @Override
        protected Output initialValue() {
            return new Output(OUTPUT_BUFFER_SIZE);
        }
    };

    private static final int INPUT_BUFFER_SIZE = 1024 * 1024 * 6;

    private static ThreadLocal<Input> inputThreadLocal = new ThreadLocal() {
        @Override
        protected Input initialValue() {
            return new Input(INPUT_BUFFER_SIZE);
        }
    };



    public static <T> byte[] serialize(T obj) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
        Kryo kryo = kryoThreadLocal.get();
        Output output = outputThreadLocal.get();
        output.reset();
        kryo.writeObject(output, obj);
        byte[] ret = output.toBytes();
        return ret;
    }


    public static <T> T deserialize(byte[] data, Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException, ClassNotFoundException {
        Kryo kryo = kryoThreadLocal.get();
        Input input = inputThreadLocal.get();
        input.reset();
        input.setBuffer(data);
        T obj = kryo.readObject(input, clazz);
        return obj;
    }

}
