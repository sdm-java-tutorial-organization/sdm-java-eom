package com.excel.eom.util.callback;

import com.excel.eom.model.FieldInfo;

import java.lang.reflect.Field;

public interface FieldInfoCallback {

    void getFieldInfo (Field field, FieldInfo fieldInfo);

}
