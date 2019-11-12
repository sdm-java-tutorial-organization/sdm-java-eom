package com.excel.eom.exception.development;

import com.excel.eom.exception.EOMDevelopmentException;
import com.excel.eom.exception.EOMRuntimeException;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ToString(callSuper = true)
public class EOMObjectException extends EOMDevelopmentException {

    public static final String MESSAGE = "The Excel Object is invalid.";
    public static final int CODE = 101;

    public EOMObjectException(Map<String, String> args) {
        super(MESSAGE, null, CODE, args);
    }

    /**
     * getArguments
     *
     * @param className
     * @param exception
     * */
    public static Map getArguments(String className, String exception) {
        Map<String, String> args = new HashMap<>();
        args.put("class", className);
        args.put("exception", exception);
        return args;
    }

}
