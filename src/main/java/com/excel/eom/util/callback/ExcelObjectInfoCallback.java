package com.excel.eom.util.callback;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.IndexedColors;


public interface ExcelObjectInfoCallback {

    void getClassInfo(String name,
                      IndexedColors cellColor,
                      short borderStyle,
                      IndexedColors borderColor,
                      int fixedRowCount,
                      int fixedColumnCount);

}
