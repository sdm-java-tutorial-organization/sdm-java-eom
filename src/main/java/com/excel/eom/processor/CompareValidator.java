package com.excel.eom.processor;

import com.excel.eom.exception.object.EOMObjectWrongDataTypeException;
import com.excel.eom.constant.deprecated.OldExcelColumn;
import com.excel.eom.constant.deprecated.OldExcelObject;
import com.excel.eom.exception.object.EOMObjectNotFoundFieldException;

import java.lang.reflect.Field;

@Deprecated
public class CompareValidator {

    /**
     * compare
     *
     * @param columnType
     * @param objectType
     * */
    public static <T extends OldExcelColumn, V extends OldExcelObject> void compare(Class<T> columnType,
                                                                                    Class<V> objectType)
            throws EOMObjectWrongDataTypeException, EOMObjectNotFoundFieldException {
        T[] columns = columnType.getEnumConstants();
        for(T column : columns) {
            String columnFieldName = column.getField();
            Class columnFieldType = column.getType();
            try {
                Field field = objectType.getField(columnFieldName);
                Class fieldType = field.getType();
                if(columnFieldType.equals(fieldType) == false) {
                    throw new EOMObjectWrongDataTypeException(null, columnFieldType.getSimpleName(), fieldType.getSimpleName());
                }
            } catch (NoSuchFieldException e) {
                throw new EOMObjectNotFoundFieldException(e, objectType.getSimpleName(), columnFieldName);
            }
        }
    }
}
