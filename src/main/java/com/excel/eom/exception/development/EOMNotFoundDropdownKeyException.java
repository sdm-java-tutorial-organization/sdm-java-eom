package com.excel.eom.exception.development;

import com.excel.eom.exception.EOMDevelopmentException;

public class EOMNotFoundDropdownKeyException extends EOMDevelopmentException {

    public static final String MESSAGE = "Not Found Dropdown key.";
    public static final int CODE = 203;

    public EOMNotFoundDropdownKeyException (int row, int column, String... args) {
        /*super(MESSAGE, CODE, row, column, args);*/
    }

}
