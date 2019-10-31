package com.excel.eom.annotation;

public @interface UniqueKeys {

    UniqueKey[] value();
    int objectKeyIndex() default 0;

}
