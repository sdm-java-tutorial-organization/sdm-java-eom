package com.excel.eom.exception.body;


import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@ToString(callSuper = true)
public class EOMNotNullException extends EOMCellException {

    public static final String MESSAGE = "Do not allow null(empty).";
    public static final int CODE = 302;

    public EOMNotNullException(Map<String, String> args) {
        super(MESSAGE, null, CODE, args);
    }

    /**
     * getArguments
     *
     * @param row
     * @param column
     * */
    public static Map getArguments(int row, int column) {
        Map<String, String> args = new HashMap<>();
        args.put("row", row + "");
        args.put("column", column + "");
        return args;
    }

}
