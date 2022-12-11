package tong.trpc.core.io.serialize;

import java.io.*;

public class TrpcJdkSerializer implements ITrpcSerializer {

    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        oos = new ObjectOutputStream(bos);
        oos.writeObject(obj); //序列化
        return bos.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        return (T) ois.readObject();
    }


    @Override
    public byte getType() {
        return TrpcSerialType.TrpcJdkSerializer.getCode();
    }
}
