package tong.trpc.core.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tong.trpc.core.io.serialize.jackson.TrpcResponseJacksonDeserializer;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(using = TrpcResponseJacksonDeserializer.class)
public class TrpcResponse  implements Serializable {

    private Object data;

    private String msg;

    private int code;

    private String returnType;
}
