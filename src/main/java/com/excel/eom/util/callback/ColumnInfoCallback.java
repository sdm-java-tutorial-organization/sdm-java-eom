package com.excel.eom.util.callback;

import java.lang.reflect.Field;

public interface ColumnInfoCallback {

    void getFieldAnnoInfo(Field field,
                          String name,
                          Integer index,
                          Integer group);

}
