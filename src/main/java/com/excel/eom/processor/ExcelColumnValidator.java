package com.excel.eom.processor;

import com.excel.eom.annotation.deprecated.AnnotationExcelColumn;
import com.excel.eom.constant.deprecated.OldExcelColumn;
import com.excel.eom.exception.document.EOMNoDocumentException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Deprecated
public class ExcelColumnValidator {

    private static final Class ANNOTATION = AnnotationExcelColumn.class;
    private static final Class[] FIELD_ANNOTATION = new Class[] { AnnotationExcelColumn.class, AnnotationExcelColumn.class, AnnotationExcelColumn.class };
    private static final List<String> LIST_FIELD_NAME = Arrays.asList("order", "field", "type");
    private static final List<String> LIST_METHOD_NAME = Arrays.asList("getOrder", "getField", "getType");


    /**
     * checkExcelColumn
     *
     * @param columnType
     * */
    @Deprecated
    public static <T extends OldExcelColumn> void checkType(Class<T> columnType)
            throws EOMNoDocumentException {
        if(OldExcelColumn.isExcelColumn(columnType) == false) {
            throw new EOMNoDocumentException(null, columnType.getSimpleName());
        }
    }

    /**
     * validateExcelColumn
     *
     * */
    public static void validateExcelColumn(Class<?> type) {
        Annotation annotation = type.getAnnotation(ANNOTATION);
        if(annotation != null) {
            if(type.isEnum()) {
                Field[] fields = type.getDeclaredFields();
                List<String> fieldNames = Arrays.stream(fields).map(Field::getName).collect(Collectors.toList());

                for(String field : LIST_FIELD_NAME) {
                    if(fieldNames.indexOf(field) < 0) {
                        // Exception
                    }
                }
            } else {
                // Excetpion
            }
        }
    }
}
