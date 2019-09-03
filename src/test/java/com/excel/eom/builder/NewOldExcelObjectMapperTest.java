package com.excel.eom.builder;

import com.excel.eom.tutorial.model.NewExcelObjectDemo;
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

public class NewOldExcelObjectMapperTest {

    File file = new File("src/main/resources/sample/sample_multiple.xlsx");
    File fileGroup = new File("src/main/resources/sample/sample_multiple.xlsx");

    List<NewExcelObjectDemo> items = Arrays.asList(
            new NewExcelObjectDemo("marvel", "nameA", 1, 101),
            new NewExcelObjectDemo("marvel", "nameB", 2, 102),
            new NewExcelObjectDemo("marvel", "nameC", 3, 103),
            new NewExcelObjectDemo("sony", "nameD", 4, 104),
            new NewExcelObjectDemo("sony", "nameE", 5, 105),
            new NewExcelObjectDemo("sony", "nameF", 6, 106),
            new NewExcelObjectDemo("sony", "nameG", 7, 107),
            new NewExcelObjectDemo("desney", "nameH", 8, 108),
            new NewExcelObjectDemo("desney", "nameI", 9, 109),
            new NewExcelObjectDemo("desney", "nameJ", 10, 110),
            new NewExcelObjectDemo("desney", "nameK", 11, 111)
    );

    @Test
    public void buildObject() throws Throwable {
        XSSFWorkbook book = new XSSFWorkbook();
        XSSFSheet sheet = ExcelSheetUtil.initSheet(book, "sheet");
        ExcelObjectMapper.init()
                .initModel(NewExcelObjectDemo.class)
                .initSheet(sheet)
                .buildObject(items);

        // print-sheet TODO value 확인안됨 -> region 확인
        ExcelSheetUtil.print(sheet);
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
        List<NewExcelObjectDemo> items = ExcelObjectMapper.init()
                .initModel(NewExcelObjectDemo.class)
                .initSheet(sheet)
                .buildSheet();

        // print-object
        for(NewExcelObjectDemo item : items) {
            System.out.println(item.toString());
        }
    }

    @Test
    public void buildSheetWithGroup() throws Throwable {
        XSSFWorkbook book = ExcelFileUtil.readExcelByFile(fileGroup);
        XSSFSheet sheet = ExcelSheetUtil.getSheet(book, 0);

        // print-sheet
        ExcelSheetUtil.print(sheet);

        List<NewExcelObjectDemo> items = ExcelObjectMapper.init()
                .initModel(NewExcelObjectDemo.class)
                .initSheet(sheet)
                .buildSheet();

        // print-object
        for(NewExcelObjectDemo item : items) {
            System.out.println(item.toString());
        }
    }


}
