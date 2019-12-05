package com.excel.eom.util;

import com.excel.eom.annotation.UniqueKey;
import com.excel.eom.annotation.UniqueKeys;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UniqueKeyUtil {

    /**
     * getUniqueValues - 설정된 UniqueKey 항목을 리스트로 반환
     *
     * @param clazz
     * @return List[key]
     * */
    public static List<List<String>> getUniqueValues(Class<?> clazz) {
        List<List<String>> result = new ArrayList<>();
        if (clazz.isAnnotationPresent(UniqueKey.class)) {
            List<String> value = Arrays.asList(clazz.getAnnotation(UniqueKey.class).value());
            if(value.size() != 1 || value.get(0).equals("") == false) {
                result.add(value);
            }
        }
        else if (clazz.isAnnotationPresent(UniqueKeys.class)) {
            UniqueKey[] uniqueKeyList = clazz.getAnnotation(UniqueKeys.class).value();
            for(UniqueKey uniqueKey : uniqueKeyList) {
                List<String> value = Arrays.asList(uniqueKey.value());
                if(value.size() != 1 || value.get(0).equals("") == false) {
                    result.add(value);
                }
            }
        }
        return result;
    }

    /**
     * validateField
     *
     * @param fields
     * @param uniqueKeys
     * */
    public static boolean validateField(Field[] fields, List<List<String>> uniqueKeys) {
        List<String> fieldNames = Arrays.stream(fields)
                .map(field-> field.getName()).collect(Collectors.toList());
        for(List<String> uniqueKey : uniqueKeys) {
            for(String field : uniqueKey) {
                if(fieldNames.indexOf(field) < 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * getWrongField
     *
     * @param fields
     * @param uniqueKeys
     * */
    public static String getWrongField(Field[] fields, List<List<String>> uniqueKeys) {
        List<String> fieldNames = Arrays.stream(fields)
                .map(field-> field.getName()).collect(Collectors.toList());
        for(List<String> uniqueKey : uniqueKeys) {
            for(String field : uniqueKey) {
                if(fieldNames.indexOf(field) < 0) {
                    return field;
                }
            }
        }
        return null;
    }

    /**
     * m1
     *
     * */
    public static void m3() {

    }
}
