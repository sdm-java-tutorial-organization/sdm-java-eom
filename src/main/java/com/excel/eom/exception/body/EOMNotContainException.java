package com.excel.eom.exception.body;

import com.excel.eom.exception.EOMBodyException;
import com.excel.eom.exception.EOMRuntimeException;
import lombok.NoArgsConstructor;

public class EOMNotContainException extends EOMCellException {

    public static final String MESSAGE = "There is content that is not included in option";
    public static final int CODE = 202;

    public EOMNotContainException() {
        super();
    }

    public EOMNotContainException(String message) {
        super(message);
    }

    public EOMNotContainException(String message, Throwable cause) {
        super(message, cause);
    }

    public EOMNotContainException(Throwable cause) {
        super(cause);
    }

    public EOMNotContainException(int row, int column, String... args) {
        super(MESSAGE, CODE, row, column, args);
    }

}
