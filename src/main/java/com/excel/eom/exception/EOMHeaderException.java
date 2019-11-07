package com.excel.eom.exception;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ToString(callSuper = true)
public class EOMHeaderException extends EOMRuntimeException {

    public static final String MESSAGE = "EOMHeaderException";

    public EOMHeaderException(Map<String, String> args) {
        super(MESSAGE, null, args);
    }

}
