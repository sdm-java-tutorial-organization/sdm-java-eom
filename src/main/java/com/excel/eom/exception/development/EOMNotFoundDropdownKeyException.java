package com.excel.eom.exception.development;

import com.excel.eom.exception.EOMDevelopmentException;
import lombok.Data;
import lombok.ToString;

import java.util.Map;

@ToString(callSuper = true)
public class EOMNotFoundDropdownKeyException extends EOMDevelopmentException {

    public static final String MESSAGE = "Not Found Dropdown key.";
    public static final int CODE = 105;

    public EOMNotFoundDropdownKeyException(Map<String, String> args) {
        super(MESSAGE, null, args);
    }

}
