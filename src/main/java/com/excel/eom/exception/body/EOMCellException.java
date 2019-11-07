package com.excel.eom.exception.body;

import com.excel.eom.exception.EOMRuntimeException;
import lombok.Data;
import lombok.ToString;

import java.util.Map;

@ToString(callSuper = true)
public abstract class EOMCellException extends EOMRuntimeException {

    public EOMCellException(String message, Throwable cause, int code, Map<String, String> args) {
        super(message, cause, code, args);
    }

}
