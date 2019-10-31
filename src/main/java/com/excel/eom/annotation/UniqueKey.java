package com.excel.eom.annotation;

import java.lang.annotation.Repeatable;

@Repeatable(UniqueKeys.class)
public @interface UniqueKey {

    String[] value() default "";

}
