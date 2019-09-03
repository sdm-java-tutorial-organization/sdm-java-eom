package com.excel.eom.util;

import com.excel.eom.annotation.ExcelColumn;
import com.excel.eom.util.callback.ColumnInfoCallback;

import static com.excel.eom.util.CollectionUtil.isEmpty;

import java.lang.reflect.Field;

public class ReflectionUtil {

    public static void getFieldAnnoInfo(Class<?> clazz, ColumnInfoCallback callback) throws Throwable {
        Field[] fields = clazz.getDeclaredFields();
        if (!isEmpty(fields)) {
            for (Field field : fields) {
                if (field.isAnnotationPresent(ExcelColumn.class)) {
                    callback.getFieldAnnoInfo(field,
                            field.getAnnotation(ExcelColumn.class).name(),
                            field.getAnnotation(ExcelColumn.class).index(),
                            field.getAnnotation(ExcelColumn.class).group()
                    );
                }
            }
        }
    }

}
