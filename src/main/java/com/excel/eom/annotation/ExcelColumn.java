package com.excel.eom.annotation;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.IndexedColors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * - only enum class
 * - force implements interface or force interface injection or force method injection
 * - force boxed data type
 * - option(excelColumn) - mapping data type with enum
 *
 *
 * */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelColumn {

    String name() default "";

    int index() default 0;

    int group() default 0;

    IndexedColors cellColor() default IndexedColors.WHITE;

    BorderStyle borderStyle() default BorderStyle.THIN;

    IndexedColors borderColor() default IndexedColors.BLACK;

    /*String pattern() default "";*/

}
