package com.excel.eom.util;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class ExcelRowUtilTest {

    File file = new File("src/main/resources/sample/sample_multiple.xlsx");

    @Test
    public void initRow() {
    }

    @Test
    public void getRow() {
    }

    @Test
    public void setRowValue() {
    }

    @Test
    public void getRowNum() throws Throwable{
        XSSFWorkbook book = ExcelFileUtil.readExcelByFile(file);
        XSSFSheet sheet = ExcelSheetUtil.getSheet(book, 0);

        XSSFRow row = ExcelRowUtil.getRow(sheet, 2);
        assertEquals(ExcelRowUtil.getRowNum(row), 2);

        row = ExcelRowUtil.getRow(sheet, 3);
        assertEquals(ExcelRowUtil.getRowNum(row), 3);
    }
}