package com.excel.eom.exception.body;

public class EOMWrongDataTypeException extends EOMCellException {

    public static final String MESSAGE = "Wrong data type.";
    public static final int CODE = 201;

    public EOMWrongDataTypeException() {
        super();
    }

    public EOMWrongDataTypeException(String message) {
        super(message);
    }

    public EOMWrongDataTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public EOMWrongDataTypeException(Throwable cause) {
        super(cause);
    }

    public EOMWrongDataTypeException(Throwable cause, int row, int column, String... args) {
        super(MESSAGE, cause, CODE, row, column, args);
    }

}
