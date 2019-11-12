package com.excel.eom.excel;

import com.excel.eom.util.ExcelFileUtil;
import com.excel.eom.util.ExcelSheetUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

@Deprecated
public class SampleGroup {

    @Test
    public void readExcel() throws IOException {
        File file = new File("src/main/resources/sample/sample_group.xlsx");
        Workbook book = ExcelFileUtil.getXSSFWorkbookByFile(file);
        Sheet sheet = book.getSheetAt(0);
        ExcelSheetUtil.print(sheet);
    }
}
