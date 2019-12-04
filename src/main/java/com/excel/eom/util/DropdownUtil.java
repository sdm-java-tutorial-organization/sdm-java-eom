package com.excel.eom.util;

import com.excel.eom.exception.development.EOMNotFoundDropdownKeyException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DropdownUtil {

    /**
     * getEnumToArray
     *
     * @param field
     * */
    public static String[] getEnumToArray(Field field) {
        Object[] enums = field.getType().getEnumConstants();
        List<String> items = new ArrayList<>();
        for (Object e : enums) {
            items.add(e.toString());
        }
        return items.toArray(new String[]{});
    }

    public static void get() {

    }

}
