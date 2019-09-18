package com.excel.eom.util.callback;

import com.excel.eom.model.ColumnElement;

import java.lang.reflect.Field;

public interface ColumnElementCallback {

    void getElement (Field field, ColumnElement element);

}
