package com.excel.eom.util;

import com.excel.eom.annotation.ExcelColumn;
import com.excel.eom.annotation.ExcelObject;
import com.excel.eom.util.callback.ExcelColumnInfoCallback;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;

import static com.excel.eom.util.CollectionUtil.isEmpty;

import java.lang.reflect.Field;

public class ReflectionUtil {

    public static boolean isSameClass(Class<?> clazzA, Class<?> classB) {
        return clazzA.getSimpleName().equals(classB.getSimpleName());
    }

    public static Object getFieldValue(Field field, Object object) {
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    public static void getFieldInfo(Class<?> clazz, ExcelColumnInfoCallback callback) {
        Field[] fields = clazz.getDeclaredFields();
        if (!isEmpty(fields)) {
            for (Field field : fields) {
                if (field.isAnnotationPresent(ExcelColumn.class)) {
                    callback.getFieldInfo(field,
                            field.getAnnotation(ExcelColumn.class).name(),
                            field.getAnnotation(ExcelColumn.class).index(),
                            field.getAnnotation(ExcelColumn.class).group(),
                            field.getAnnotation(ExcelColumn.class).dropdown(),
                            field.getAnnotation(ExcelColumn.class).nullable(),
                            field.getAnnotation(ExcelColumn.class).width(),
                            field.getAnnotation(ExcelColumn.class).alignment(),
                            field.getAnnotation(ExcelColumn.class).cellColor(),
                            field.getAnnotation(ExcelColumn.class).borderStyle(),
                            field.getAnnotation(ExcelColumn.class).borderColor()
                    );
                }
            }
        }
    }

}
