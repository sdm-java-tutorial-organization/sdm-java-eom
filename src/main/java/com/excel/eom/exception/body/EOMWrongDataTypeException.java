package com.excel.eom.exception.body;

import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@ToString(callSuper = true)
public class EOMWrongDataTypeException extends EOMCellException {

    public static final String MESSAGE = "Wrong data type.";
    public static final int CODE = 303;

    public EOMWrongDataTypeException(Map<String, String> args) {
        super(MESSAGE, null, CODE, args);
    }

    /**
     * getArguments
     *
     * @param row
     * @param column
     * */
    public static Map getArguments(Object value, int row, int column) {
        Map<String, String> args = new HashMap<>();
        args.put("value", value + "");
        args.put("row", row + "");
        args.put("column", column + "");
        return args;
    }

}
