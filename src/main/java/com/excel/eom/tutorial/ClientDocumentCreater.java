package com.excel.eom.tutorial;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;

public class ClientDocumentCreater {

    /**
     * createDocumentDummy
     *
     * @param dummy
     * */
    public static XSSFWorkbook createDocumentDummy(List<List> dummy) {
        XSSFWorkbook book = new XSSFWorkbook();
        XSSFSheet sheet = book.createSheet();
        for(int i=0; i<dummy.size(); i++) {
            XSSFRow row = sheet.createRow(i);
            List rowDummy = dummy.get(i);
            for(int j=0; j<rowDummy.size(); j++) {
                XSSFCell cell = null;
                switch (rowDummy.get(j).getClass().getSimpleName()) {
                    case "String":
                        cell = row.createCell(j, Cell.CELL_TYPE_STRING);
                        cell.setCellValue((String) rowDummy.get(j));
                        break;
                    case "Integer":
                        cell = row.createCell(j, Cell.CELL_TYPE_NUMERIC);
                        cell.setCellValue((Integer) rowDummy.get(j));
                        break;
                }

            }
        }
        return book;
    }
}
