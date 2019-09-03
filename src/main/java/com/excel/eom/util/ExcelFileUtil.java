package com.excel.eom.util;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelFileUtil {

    /**
     * readExcelByFile
     *
     * @param file
     * */
    public static XSSFWorkbook readExcelByFile(File file) throws IOException {
        FileInputStream excelFile = new FileInputStream(file);
        return new XSSFWorkbook(excelFile);
    }

    /**
     * writeExcel
     *
     * @param book
     * @param path
     */
    public static void writeExcel(XSSFWorkbook book, String path) throws IOException {
        File file = new File(path);
        FileOutputStream fos = new FileOutputStream(file);
        book.write(fos);
        fos.close();
    }

}
