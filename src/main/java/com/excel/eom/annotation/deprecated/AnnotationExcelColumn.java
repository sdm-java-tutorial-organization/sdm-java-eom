package com.excel.eom.annotation.deprecated;

import lombok.AllArgsConstructor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * - only enum type
 * - force implements interface & override method
 * - force some filed
 * - option
 *
 *
 * */
@Deprecated
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface AnnotationExcelColumn {

    Class<?> mapping();

}
