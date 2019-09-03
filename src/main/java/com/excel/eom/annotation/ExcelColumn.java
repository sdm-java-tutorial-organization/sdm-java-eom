package com.excel.eom.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
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
@Retention(RUNTIME)
public @interface ExcelColumn {

    String name() default "";

    int index() default 0;

    int group() default 0;

    /*String pattern() default "";*/

}
