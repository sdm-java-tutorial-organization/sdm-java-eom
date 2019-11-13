package com.excel.eom.exception.development;

import com.excel.eom.exception.EOMDevelopmentException;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@ToString(callSuper = true)
public class EOMWrongListException extends EOMDevelopmentException {

    public static final String MESSAGE = "The class in the list is different from the initialized model.";
    public static final int CODE = 101;

    public EOMWrongListException(Map<String, String> args) {
        super(MESSAGE, null, CODE, args);
    }

    /**
     * getArguments
     *
     * @param modelClass
     * @param listClass
     * */
    public static Map getArguments(String modelClass, String listClass) {
        Map<String, String> args = new HashMap<>();
        args.put("modelClass", modelClass);
        args.put("listClass", listClass);
        return args;
    }

}
