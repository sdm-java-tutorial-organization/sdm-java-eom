package com.excel.eom.exception;

import lombok.ToString;
import java.util.Map;

@ToString(callSuper = true)
public class EOMHeaderException extends EOMRuntimeException {

    public static final String MESSAGE = "EOMHeaderException";

    public static final int CODE = 201;

    public EOMHeaderException(String sheet, Map<String, String> args) {
        super(MESSAGE, null, CODE, sheet, args);
    }

}
