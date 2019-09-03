package com.excel.eom.util;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class ExcelFileUtilTest {

    @Test
    public void readExcelByFile() {
    }

    @Test
    public void writeExcel() throws IOException {
        XSSFWorkbook book = new XSSFWorkbook();
        ExcelFileUtil.writeExcel(book, "src/main/resources/deploy/deploy.xlsx");
    }
}