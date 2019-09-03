package com.excel.eom.annotation.deprecated;

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
@Deprecated
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface AnnotationExcelObject {

    Class<?> column();

}
