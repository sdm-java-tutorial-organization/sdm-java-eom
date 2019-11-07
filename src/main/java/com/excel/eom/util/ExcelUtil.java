package com.excel.eom.util;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class ExcelUtil {

    public static XSSFWorkbook initBook() {
        return new XSSFWorkbook();
    }

}
