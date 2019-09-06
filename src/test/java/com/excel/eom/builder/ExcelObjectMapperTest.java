package com.excel.eom.builder;

import com.excel.eom.tutorial.ExcelObjectDemo;
import com.excel.eom.util.ExcelFileUtil;
import com.excel.eom.util.ExcelRegionUtil;
import com.excel.eom.util.ExcelSheetUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ExcelObjectMapperTest {

    File file = new File("src/main/resources/sample/sample_multiple.xlsx");
    File fileGroup = new File("src/main/resources/sample/sample_multiple.xlsx");

    List<ExcelObjectDemo> items = Arrays.asList(
            new ExcelObjectDemo("marvel", "nameA", 1, 101),
            new ExcelObjectDemo("marvel", "nameB", 2, 102),
            new ExcelObjectDemo("marvel", "nameC", 3, 103),
            new ExcelObjectDemo("sony", "nameD", 4, 104),
            new ExcelObjectDemo("sony", "nameE", 5, 105),
            new ExcelObjectDemo("sony", "nameF", 6, 106),
            new ExcelObjectDemo("sony", "nameG", 7, 107),
            new ExcelObjectDemo("desney", "nameH", 8, 108),
            new ExcelObjectDemo("desney", "nameI", 9, 109),
            new ExcelObjectDemo("desney", "nameJ", 10, 110),
            new ExcelObjectDemo("desney", "nameK", 11, 111)
    );

    @Test
    public void buildObject() throws Throwable {
        XSSFWorkbook book = new XSSFWorkbook();
        XSSFSheet sheet = ExcelSheetUtil.initSheet(book, "sheet");
        ExcelObjectMapper.init()
                .initModel(ExcelObjectDemo.class)
                .initBook(book)
                .initSheet(sheet)
                .buildObject(items);

        // print-sheet TODO value 확인안됨 -> region 확인
        assertEquals(sheet.getNumMergedRegions(), 3);
        ExcelRegionUtil.print(sheet);

        // write
        ExcelFileUtil.writeExcel(book, "src/main/resources/deploy/deploy_multiple.xlsx");
    }

    @Test
    public void test() throws Throwable {
        File file = new File("src/main/resources/deploy/deploy_multiple.xlsx");
        XSSFWorkbook bookDeploy = ExcelFileUtil.readExcelByFile(file);
        XSSFSheet sheetDeploy = ExcelSheetUtil.getSheet(bookDeploy, 0);

        // print-sheet
        ExcelSheetUtil.print(sheetDeploy);
    }

    @Test
    public void buildSheet() throws Throwable {
        XSSFWorkbook book = ExcelFileUtil.readExcelByFile(file);
        XSSFSheet sheet = ExcelSheetUtil.getSheet(book, 0);
        ExcelSheetUtil.print(sheet);

        // print-sheet
        List<ExcelObjectDemo> items = ExcelObjectMapper.init()
                .initModel(ExcelObjectDemo.class)
                .initBook(book)
                .initSheet(sheet)
                .buildSheet();

        // print-object
        for(ExcelObjectDemo item : items) {
            System.out.println(item.toString());
        }
    }

    @Test
    public void buildSheetWithGroup() throws Throwable {
        XSSFWorkbook book = ExcelFileUtil.readExcelByFile(fileGroup);
        XSSFSheet sheet = ExcelSheetUtil.getSheet(book, 0);

        // print-sheet
        ExcelSheetUtil.print(sheet);

        List<ExcelObjectDemo> items = ExcelObjectMapper.init()
                .initModel(ExcelObjectDemo.class)
                .initBook(book)
                .initSheet(sheet)
                .buildSheet();

        // print-object
        for(ExcelObjectDemo item : items) {
            System.out.println(item.toString());
        }
    }


}
