package com.excel.eom.util;


import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.File;

public class ExcelSheetUtilTest {

    @Test
    public void getSheetHeight() throws Exception {
        File file = new File("src/main/resources/sample/sample_multiple.xlsx");
        Workbook book = ExcelFileUtil.getXSSFWorkbookByFile(file);
        Sheet sheet = ExcelSheetUtil.getSheet(book, 0);
        System.out.println(ExcelSheetUtil.getSheetHeight(sheet));
    }

    @Test
    public void print() {
    }

    @Test
    public void getMergedRegion() throws Throwable {
        File file = new File("src/main/resources/sample/sample_multiple.xlsx");
        Workbook book = ExcelFileUtil.getXSSFWorkbookByFile(file);
        Sheet sheet = ExcelSheetUtil.getSheet(book, 0);
        System.out.println(sheet.getNumMergedRegions());

        file = new File("src/main/resources/sample/sample_single.xlsx");
        book = ExcelFileUtil.getXSSFWorkbookByFile(file);
        sheet = ExcelSheetUtil.getSheet(book, 0);
        System.out.println(sheet.getNumMergedRegions());
    }
}