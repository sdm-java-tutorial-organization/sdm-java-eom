package com.excel.eom.exception;

import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@ToString
@Getter
public class EOMRuntimeException extends RuntimeException {

    public Map<String, String> args;
    private int code;
    private String message;

    public EOMRuntimeException(String message, Throwable cause, int code, Map<String, String> args) {
        super(message, cause);
        this.code = code;
        this.message = message;
        this.args = args;
    }

}
