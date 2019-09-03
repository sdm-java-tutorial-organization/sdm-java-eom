package com.excel.eom.constant.deprecated;

import com.excel.eom.exception.document.EOMDocumentWrongDataTypeException;
import com.excel.eom.exception.object.EOMObjectNotFoundFieldException;

import java.lang.reflect.Field;

@Deprecated
public interface OldExcelObject {

    public static boolean isExcelObject(Class<?> clazz) {
        return OldExcelObject.class.isAssignableFrom(clazz);
    }

    /**
     * build()
     * value 값의 보장이 필요
     *
     * @param type
     * @param column
     * @param value
     * */
    public default void build(Class<?> type,
                              String column,
                              Object value)
            throws EOMObjectNotFoundFieldException, EOMDocumentWrongDataTypeException {
        String className = this.getClass().getSimpleName();
        String fieldName = column;
        String requiredTypeName = type.getSimpleName();
        try {
            Field field = this.getClass().getDeclaredField(fieldName);
            switch (requiredTypeName) {
                case "String":
                    String resultString = (String) value;
                    field.set(this, resultString);
                    break;
                case "Integer":
                    Integer resultInteger = (Integer) value;
                    field.set(this, resultInteger);
                    break;
                default:
                    throw new EOMDocumentWrongDataTypeException(null, requiredTypeName);
            }
        } catch (NoSuchFieldException e) {
            throw new EOMObjectNotFoundFieldException(e, className, fieldName);
        } catch (SecurityException e) {
            throw new EOMObjectNotFoundFieldException(e, className, fieldName);
        } catch (IllegalAccessException e) {
            throw new EOMObjectNotFoundFieldException(e, className, fieldName);
        } catch (NumberFormatException e) {
            throw new EOMDocumentWrongDataTypeException(e, requiredTypeName);
        } catch (IllegalArgumentException e) {
            throw new EOMDocumentWrongDataTypeException(e, requiredTypeName);
        } catch (ClassCastException e) {
            // TODO
        }
    }

    /**
     * getFieldValue
     *
     * @param column
     * */
    public default String getFieldValue(String column)
            throws EOMObjectNotFoundFieldException {
        Object object;
        String className = this.getClass().getSimpleName();
        String columnName = column;
        try {
            Field field = this.getClass().getDeclaredField(columnName);
            object = field.get(this);
        } catch (NoSuchFieldException e) {
            throw new EOMObjectNotFoundFieldException(e, className, columnName);
        } catch (IllegalAccessException e) {
            throw new EOMObjectNotFoundFieldException(e, className, columnName);
        }
        return String.valueOf(object);
    }

}
