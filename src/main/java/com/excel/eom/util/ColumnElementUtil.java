package com.excel.eom.util;

import com.excel.eom.model.ColumnElement;
import com.excel.eom.util.callback.ColumnElementCallback;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;

public class ColumnElementUtil {

    public static void getElement(Map<Field, ColumnElement> elementMap, ColumnElementCallback callback) throws Throwable {
        Iterator<Map.Entry<Field, ColumnElement>> iterator = elementMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Field, ColumnElement> entry = iterator.next();
            callback.getElement(entry.getKey(), entry.getValue());
        }
    }
}
