package com.excel.eom.exception.development;

import com.excel.eom.exception.EOMDevelopmentException;
import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@ToString(callSuper = true)
public class EOMNotFoundDropdownKeyException extends EOMDevelopmentException {

    public static final String MESSAGE = "Not Found Dropdown key.";
    public static final int CODE = 105;

    public EOMNotFoundDropdownKeyException(String sheet, Map<String, String> args) {
        super(MESSAGE, null, CODE, sheet, args);
    }

    /**
     * getArguments
     *
     * @param fieldName
     * @param dropdownKey
     * */
    public static Map getArguments(String fieldName, String dropdownKey) {
        Map<String, String> args = new HashMap<>();
        args.put("fieldName", fieldName);
        args.put("dropdownKey", dropdownKey);
        return args;
    }


}
