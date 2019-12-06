package com.excel.eom.exception;

import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@ToString
@Getter
public class EOMRuntimeException extends RuntimeException {

    private Map<String, String> args;

    private int code;

    private String message;

    private String sheet;

    public EOMRuntimeException(String message, Throwable cause, int code, Map<String, String> args) {
        super(message, cause);
        this.message = message;
        this.code = code;
        this.args = args;
    }

    public EOMRuntimeException(String message, Throwable cause, int code, String sheet, Map<String, String> args) {
        this(message, cause, code, args);
        this.sheet = sheet;
    }

}
