package com.excel.eom.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(UniqueKeys.class)
public @interface UniqueKey {

    String[] value() default "";

}
