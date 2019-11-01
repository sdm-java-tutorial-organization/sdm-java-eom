package com.excel.eom.util;

import com.excel.eom.model.FieldInfo;
import com.excel.eom.util.callback.FieldInfoCallback;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FieldInfoUtil {

    /**
     * getEachFieldInfo
     *
     * @param fieldInfoMap
     * @param callback
     */
    public static void getEachFieldInfo(Map<Field, FieldInfo> fieldInfoMap,
                                        FieldInfoCallback callback) {
        Iterator<Map.Entry<Field, FieldInfo>> iterator = fieldInfoMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Field, FieldInfo> entry = iterator.next();
            callback.getFieldInfo(entry.getKey(), entry.getValue());
        }
    }

    /**
     * extractFieldInfoByGroup - group extract
     *
     * @param fieldInfoMap
     */
    public static Map<Field, FieldInfo> extractFieldInfoByGroup(Map<Field, FieldInfo> fieldInfoMap) {
        return fieldInfoMap.entrySet().stream()
                .filter(fieldInfoEntry -> fieldInfoEntry.getValue().getGroup() > 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * extractFieldInfoByDropdown - enum extract
     *
     * @param fieldInfoMap
     */
    public static Map<Field, FieldInfo> extractFieldInfoByDropdown(Map<Field, FieldInfo> fieldInfoMap) {
        return fieldInfoMap.entrySet().stream()
                .filter(fieldInfoEntry -> fieldInfoEntry.getKey().getType().isEnum()
                        ||
                        fieldInfoEntry.getValue().getDropdown().equals(FieldInfo.INIT_DROPDOWN) == false)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * extractFieldInfoByRegion - enum & group extract
     *
     * @param fieldInfoMap
     */
    @Deprecated
    public static Map<Field, FieldInfo> extractFieldInfoByRegion(Map<Field, FieldInfo> fieldInfoMap) {
        return fieldInfoMap.entrySet().stream()
                .filter(fieldInfoEntry -> fieldInfoEntry.getKey().getType().isEnum()
                        ||
                        fieldInfoEntry.getValue().getGroup() > 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * map -> list with sort
     *
     * @param fieldInfoMap
     */
    public static List getElementListWithSort(Map<Field, FieldInfo> fieldInfoMap) {
        return fieldInfoMap.entrySet().stream()
                .sorted((a, b) -> a.getValue().getIndex() - b.getValue().getIndex())
                .collect(Collectors.toList());
    }
}
