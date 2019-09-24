package com.excel.eom.exception.body;

public class EOMNotDropdownKeyException extends EOMCellException {

    public static final String MESSAGE = "Not Found Dropdown key.";
    public static final int CODE = 203;

    public EOMNotDropdownKeyException(int row, int column, String... args) {
        super(MESSAGE, CODE, row, column, args);
    }

}
