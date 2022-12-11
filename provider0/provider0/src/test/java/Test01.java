import com.example.tutorial.protos.Person;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * @Author tong-exists
 * @Create 2022/12/11 15:49
 * @Version 1.0
 */
public class Test01 {

    public static void main(String[] args) throws InvalidProtocolBufferException {
        Person john =
                Person.newBuilder()
                        .setId(1234)
                        .setName("John Doe")
                        .setEmail("jdoe@example.com")
                        .addPhones(
                                Person.PhoneNumber.newBuilder()
                                        .setNumber("555-4321")
                                        .setType(Person.PhoneType.HOME))
                        .build();
        byte[] bytes = john.toByteArray();
        Person person = Person.parseFrom(bytes);
        log.info(person);

    }


}
