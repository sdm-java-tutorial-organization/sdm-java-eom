package com.excel.eom.exception.body;

public class EOMNotUniqueException extends EOMCellException {

    public static final String MESSAGE = "Not Unique.";
    public static final int CODE = 201;

    public EOMNotUniqueException() {
        super();
    }

    public EOMNotUniqueException(String message) {
        super(message);
    }

    public EOMNotUniqueException(String message, Throwable cause) {
        super(message, cause);
    }

    public EOMNotUniqueException(Throwable cause) {
        super(cause);
    }

    public EOMNotUniqueException(Throwable cause, int row, int column, String... args) {
        super(MESSAGE, cause, CODE, row, column, args);
    }

}
