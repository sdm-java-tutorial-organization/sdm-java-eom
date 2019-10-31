package com.excel.eom.exception.body;

import com.excel.eom.exception.EOMBodyException;
import com.excel.eom.exception.EOMRuntimeException;

public class EOMNotNullException extends EOMCellException {

    public static final String MESSAGE = "Do not allow null(empty).";
    public static final int CODE = 201;

    public EOMNotNullException() {
        super();
    }

    public EOMNotNullException(String message) {
        super(message);
    }

    public EOMNotNullException(String message, Throwable cause) {
        super(message, cause);
    }

    public EOMNotNullException(Throwable cause) {
        super(cause);
    }

    public EOMNotNullException(int row, int column, String... args) {
        super(MESSAGE, CODE, row, column, args);
    }

}
