package com.excel.eom.util;

import com.excel.eom.annotation.ExcelColumn;
import com.excel.eom.annotation.ExcelObject;
import com.excel.eom.util.callback.ExcelColumnInfoCallback;
import com.excel.eom.util.callback.ExcelObjectInfoCallback;

import static com.excel.eom.util.CollectionUtil.isEmpty;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class ReflectionUtil {

    public static void getClassInfo(Class<?> clazz, ExcelObjectInfoCallback callback) throws Throwable {
        if (clazz.isAnnotationPresent(ExcelObject.class)) {
            callback.getClassInfo(
                    clazz.getAnnotation(ExcelObject.class).name(),
                    clazz.getAnnotation(ExcelObject.class).cellColor(),
                    clazz.getAnnotation(ExcelObject.class).borderStyle(),
                    clazz.getAnnotation(ExcelObject.class).borderColor()
            );
        }
    }

    public static void getFieldInfo(Class<?> clazz, ExcelColumnInfoCallback callback) throws Throwable {
        Field[] fields = clazz.getDeclaredFields();
        if (!isEmpty(fields)) {
            for (Field field : fields) {
                if (field.isAnnotationPresent(ExcelColumn.class)) {
                    callback.getFieldInfo(field,
                            field.getAnnotation(ExcelColumn.class).name(),
                            field.getAnnotation(ExcelColumn.class).index(),
                            field.getAnnotation(ExcelColumn.class).group(),
                            field.getAnnotation(ExcelColumn.class).cellColor(),
                            field.getAnnotation(ExcelColumn.class).borderStyle(),
                            field.getAnnotation(ExcelColumn.class).borderColor()
                    );
                }
            }
        }
    }

}
