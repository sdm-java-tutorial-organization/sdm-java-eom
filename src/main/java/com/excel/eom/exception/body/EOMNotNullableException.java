package com.excel.eom.exception.body;

import com.excel.eom.exception.EOMBodyException;
import com.excel.eom.exception.EOMRuntimeException;

public class EOMNotNullableException extends EOMCellException {

    public static final String MESSAGE = "Do not allow null(empty).";
    public static final int CODE = 201;

    public EOMNotNullableException(int row, int column, String... args) {
        super(MESSAGE, CODE, row, column, args);
    }

}
