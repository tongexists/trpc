package tong.trpc.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class TrpcResponse  implements Serializable {

    private Object data;

    private String msg;

    private int code;

    private String returnType;
}
