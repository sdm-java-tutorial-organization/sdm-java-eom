package com.excel.eom.exception;

import lombok.Data;
import lombok.ToString;

import java.util.Map;

@ToString(callSuper = true)
public abstract class EOMDevelopmentException extends EOMRuntimeException {

    public EOMDevelopmentException(String message, Throwable cause, int code, Map<String, String> args) {
        super(message, cause, code, args);
    }

}
