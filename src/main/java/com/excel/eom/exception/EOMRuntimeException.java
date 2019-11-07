package com.excel.eom.exception;

import lombok.ToString;

import java.util.Map;

@ToString
public class EOMRuntimeException extends RuntimeException {

    public Map<String, String> args;
    private String message;

    public EOMRuntimeException(String message, Throwable cause, Map<String, String> args) {
        super(message, cause);
        this.message = message;
        this.args = args;
    }

}
