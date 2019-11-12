package com.excel.eom.util;


import com.sun.corba.se.spi.orbutil.threadpool.Work;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

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
        Workbook book = ExcelFileUtil.getXSSFWorkbookByFile(file);
        Sheet sheet = ExcelSheetUtil.getSheet(book, 0);

        Row row = ExcelRowUtil.getRow(sheet, 2);
        assertEquals(ExcelRowUtil.getRowNum(row), 2);

        row = ExcelRowUtil.getRow(sheet, 3);
        assertEquals(ExcelRowUtil.getRowNum(row), 3);
    }
}