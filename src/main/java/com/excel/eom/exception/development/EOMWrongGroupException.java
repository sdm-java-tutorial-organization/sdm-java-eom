package com.excel.eom.exception.development;

import com.excel.eom.exception.EOMDevelopmentException;
import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@ToString(callSuper = true)
public class EOMWrongGroupException extends EOMDevelopmentException {

    public static final String MESSAGE = "The Group option is invalid.";
    public static final int CODE = 103;

    public EOMWrongGroupException(Map<String, String> args) {
        super(MESSAGE, null, args);
    }

    /**
     * getArguments
     *
     * @param className
     * */
    public static Map getArguments(String className, int group) {
        Map<String, String> args = new HashMap<>();
        args.put("field", className);
        args.put("group", group + "");
        return args;
    }

}
