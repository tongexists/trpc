package tong.trpc.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum TrpcResponseCode {

    SUCCESS(1, "success"),
    ERROR(2, "error");

    private int code;

    private String msg;

}
