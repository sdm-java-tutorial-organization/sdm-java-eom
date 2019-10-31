package com.excel.eom.exception.body;

public class EOMNotFoundDropdownKeyException extends EOMCellException {

    public static final String MESSAGE = "Not Found Dropdown key.";
    public static final int CODE = 203;

    public EOMNotFoundDropdownKeyException (int row, int column, String... args) {
        /*super(MESSAGE, CODE, row, column, args);*/
    }

}
