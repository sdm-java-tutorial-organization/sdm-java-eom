package com.excel.eom.annotation;

import com.excel.eom.constant.ExcelColor;
import org.apache.poi.ss.usermodel.BorderStyle;
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

    BorderStyle borderStyle() default BorderStyle.THIN;

    IndexedColors borderColor() default IndexedColors.BLACK;

}
