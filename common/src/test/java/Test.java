import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author tong-exists
 * @Create 2022/12/11 16:52
 * @Version 1.0
 */
@Data
final class Foo {
    String name;
    int id;

    public Foo(String name, int id) {
        this.name = name;
        this.id = id;
    }
}

@Slf4j
public class Test {

    public static void main(String[] args) {
        Foo foo = new Foo("foo", 1);

        // this is lazily created and cached by RuntimeSchema
        // so its safe to call RuntimeSchema.getSchema(Foo.class) over and over
        // The getSchema method is also thread-safe
        Schema<Foo> schema = RuntimeSchema.getSchema(Foo.class);

        // Re-use (manage) this buffer to avoid allocating on every serialization
        LinkedBuffer buffer = LinkedBuffer.allocate(512);

        // ser
        final byte[] protostuff;
        try {
            protostuff = ProtostuffIOUtil.toByteArray(foo, schema, buffer);
        } finally {
            buffer.clear();
        }

        // deser
        Foo fooParsed = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(protostuff, fooParsed, schema);
        log.info("parse: {}", fooParsed);
    }
}

