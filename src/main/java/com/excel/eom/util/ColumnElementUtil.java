package com.excel.eom.util;

import com.excel.eom.model.ColumnElement;
import com.excel.eom.util.callback.ColumnElementCallback;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ColumnElementUtil {

    public static void getElement(Map<Field, ColumnElement> elementMap,
                                  ColumnElementCallback callback) {
        Iterator<Map.Entry<Field, ColumnElement>> iterator = elementMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Field, ColumnElement> entry = iterator.next();
            callback.getElement(entry.getKey(), entry.getValue());
        }
    }

    /**
     * group extract
     *
     * */
    public static Map<Field, ColumnElement> extractElementByGroup(Map<Field, ColumnElement> originElementMap) {
        return originElementMap.entrySet().stream()
                .filter(element -> element.getValue().getGroup() > 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * enum extract
     *
     * */
    public static Map<Field, ColumnElement> extractElementByEnum(Map<Field, ColumnElement> originElementMap) {
        return originElementMap.entrySet().stream()
                .filter(element -> element.getKey().getType().isEnum())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * enum & group extract
     *
     * */
    @Deprecated
    public static Map<Field, ColumnElement> getRegionElement(Map<Field, ColumnElement> originElementMap) {
         return originElementMap.entrySet().stream()
                .filter(element -> element.getKey().getType().isEnum() || element.getValue().getGroup() > 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * map -> list with sort
     *
     * */
    public static List getElementListWithSort(Map<Field, ColumnElement> elementMap) {
        return elementMap.entrySet().stream()
                .sorted((a, b) -> a.getValue().getIndex() - b.getValue().getIndex())
                .collect(Collectors.toList());
    }
}
