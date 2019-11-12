package com.excel.eom.util;


import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelFileUtil {

    /**
     * file -> XSSFWorkbook
     *
     * @param file
     * */
    public static Workbook getXSSFWorkbookByFile(File file) throws IOException {
        FileInputStream excelFile = new FileInputStream(file);
        return new XSSFWorkbook(excelFile);
    }

    /**
     * file -> SXSSFWorkbook
     *
     * @param file
     * */
    /*public static Workbook getSXSSFWorkbookByFile(File file) throws IOException {
        FileInputStream excelFile = new FileInputStream(file);
        return new SXSSFWorkbook(excelFile);
    }*/

    /**
     * writeExcel
     *
     * @param book
     * @param path
     */
    public static void writeExcel(Workbook book, String path) throws IOException {
        File file = new File(path);
        FileOutputStream fos = new FileOutputStream(file);
        book.write(fos);
        fos.close();
    }

}
