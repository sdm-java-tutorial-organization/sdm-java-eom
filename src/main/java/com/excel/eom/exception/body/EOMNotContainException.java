package com.excel.eom.exception.body;

import com.excel.eom.exception.EOMBodyException;
import com.excel.eom.exception.EOMRuntimeException;

public class EOMNotContainException extends EOMCellException {

    public static final String MESSAGE = "There is content that is not included in option";
    public static final int CODE = 202;

    public EOMNotContainException(int row, int column, String... args) {
        super(MESSAGE, CODE, row, column, args);
    }

}
