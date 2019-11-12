package com.excel.eom.util.callback;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.IndexedColors;

import java.lang.reflect.Field;

public interface ExcelColumnInfoCallback {

    void getFieldInfo(Field field,
                      String name,
                      Integer index,
                      Integer group,
                      String dropdown,
                      Boolean nullable,
                      Integer width,
                      Short alignment,
                      IndexedColors cellColor,
                      short borderStyle,
                      IndexedColors borderColor);

}
