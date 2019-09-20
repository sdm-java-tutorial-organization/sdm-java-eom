package com.excel.eom.util;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.List;

public class ExcelRowUtil {

    /**
     * initRow
     *
     * @param sheet
     * @param index
     */
    public static XSSFRow initRow(XSSFSheet sheet,
                                  int index) {
        return sheet.createRow(index);
    }

    /**
     * getRow
     *
     * @param sheet
     * @param index
     */
    public static XSSFRow getRow(XSSFSheet sheet,
                                 int index) {
        return sheet.getRow(index);
    }

    /**
     * setRowValue
     *
     * @param row
     * @param values
     * */
    public static void setRowValue(XSSFRow row,
                                   List<String> values) {
        if(values != null && values.size() > 1) {
            for(int i=0; i<values.size(); i++) {
                XSSFCell cell = row.createCell(i);
                cell.setCellValue(values.get(i));
            }
        }
    }

    /**
     * checkRowEmpty
     *
     * @param row
     * */
    public static boolean isEmptyRow(XSSFRow row) {
        boolean isEmpty = true;
        for(int i=0; i<row.getPhysicalNumberOfCells(); i++) {
            XSSFCell cell = ExcelCellUtil.getCell(row, i);
            if(cell == null) {
                continue;
            } else {
                Object cellValue = ExcelCellUtil.getCellValue(cell);
                if(cellValue == null || cellValue.equals("")) {
                    continue;
                } else {
                    isEmpty = false;
                    break;
                }
            }
        }
        return isEmpty;
    }

    /**
     * getRowNum
     *
     * @param row
     * */
    public static int getRowNum(XSSFRow row) {
        return row.getRowNum();
    }

}
