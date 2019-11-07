package com.excel.eom.exception.development;

import com.excel.eom.exception.EOMDevelopmentException;
import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@ToString(callSuper = true)
public class EOMWrongIndexException extends EOMDevelopmentException {

    public static final String MESSAGE = "The Index Option is invalid.";
    public static final int CODE = 101;

    public EOMWrongIndexException(Map<String, String> args) {
        super(MESSAGE, null, args);
    }

    /**
     * getArguments
     *
     * @param index
     * */
    public static Map getArguments(String className, int index) {
        Map<String, String> args = new HashMap<>();
        args.put("class", className);
        args.put("index", index + "");
        return args;
    }

}

