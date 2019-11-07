package com.excel.eom.exception.body;

import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@ToString(callSuper = true)
public class EOMNotUniqueException extends EOMCellException {

    public static final String MESSAGE = "There is a non-unique value in a unique field.";
    public static final int CODE = 305;

    public EOMNotUniqueException(Map<String, String> args) {
        super(MESSAGE, null, args);
    }

    /**
     * getArguments
     *
     * @param value
     * @param row
     * */
    public static Map getArguments(Object value, int row) {
        Map<String, String> args = new HashMap<>();
        args.put("unique", value + "");
        args.put("row", row + "");
        return args;
    }

}
