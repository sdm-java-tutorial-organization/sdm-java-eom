package com.excel.eom.annotation;

import com.excel.eom.constant.CellColor;

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
@Retention(RetentionPolicy.SOURCE)
public @interface ExcelObject {

    String name() default "sheet";

    CellColor color() default CellColor.GREEN;

}
