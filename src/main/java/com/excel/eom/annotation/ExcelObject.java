package com.excel.eom.annotation;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * - only enum class
 * - force implements interface or force interface injection or force method injection
 * - force boxed data type
 * - option(excelColumn) - mapping data type with enum
 *
 *
 * */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelObject {

    String name() default "sheet";

    // TODO compile error
    String[] uniqueKeys() default "";

    IndexedColors cellColor() default IndexedColors.YELLOW;

    short borderStyle() default CellStyle.BORDER_THIN;

    IndexedColors borderColor() default IndexedColors.BLACK;

    int fixedRowCount() default 0;

    int fixedColumnCount() default 0;

}
