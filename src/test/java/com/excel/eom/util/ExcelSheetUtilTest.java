package com.excel.eom.util;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class ExcelSheetUtilTest {

    @Test
    public void getSheetHeight() throws Exception {
        File file = new File("src/main/resources/sample/sample_multiple.xlsx");
        XSSFWorkbook book = ExcelFileUtil.readExcelByFile(file);
        XSSFSheet sheet = ExcelSheetUtil.getSheet(book, 0);
        System.out.println(ExcelSheetUtil.getSheetHeight(sheet));
    }

    @Test
    public void print() {
    }

    @Test
    public void getMergedRegion() throws Throwable {
        File file = new File("src/main/resources/sample/sample_multiple.xlsx");
        XSSFWorkbook book = ExcelFileUtil.readExcelByFile(file);
        XSSFSheet sheet = ExcelSheetUtil.getSheet(book, 0);
        System.out.println(sheet.getNumMergedRegions());

        file = new File("src/main/resources/sample/sample_single.xlsx");
        book = ExcelFileUtil.readExcelByFile(file);
        sheet = ExcelSheetUtil.getSheet(book, 0);
        System.out.println(sheet.getNumMergedRegions());
    }
}