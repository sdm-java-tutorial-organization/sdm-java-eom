package com.excel.eom.exception.development;

import com.excel.eom.exception.EOMDevelopmentException;
import lombok.Data;
import lombok.ToString;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.HashMap;
import java.util.Map;

@ToString(callSuper = true)
public class EOMWrongUniqueFieldException extends EOMDevelopmentException {

    public static final String MESSAGE = "Wrong Field in UniqueKey Annotation.";
    public static final int CODE = 0;

    public EOMWrongUniqueFieldException(String sheet, Map<String, String> args) {
        super(MESSAGE, null, CODE, sheet, args);
    }

    /**
     * getArguments
     *
     * @param fieldName
     * */
    public static Map getArguments(String fieldName) {
        Map<String, String> args = new HashMap<>();
        args.put("field", fieldName);
        return args;
    }

}
