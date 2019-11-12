package com.excel.eom.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;

public class ExcelRowUtil {

    /**
     * initRow
     *
     * @param sheet
     * @param index
     */
    public static Row initRow(Sheet sheet,
                              int index) {
        return sheet.createRow(index);
    }

    /**
     * getRow
     *
     * @param sheet
     * @param index
     */
    public static Row getRow(Sheet sheet,
                                 int index) {
        return sheet.getRow(index);
    }

    /**
     * setRowValue
     *
     * @param row
     * @param values
     * */
    public static void setRowValue(Row row,
                                   List<String> values) {
        if(values != null && values.size() > 1) {
            for(int i=0; i<values.size(); i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(values.get(i));
            }
        }
    }

    /**
     * checkRowEmpty
     *
     * @param row
     * */
    public static boolean isEmptyRow(Row row) {
        boolean isEmpty = true;
        for(int i=0; i<row.getPhysicalNumberOfCells(); i++) {
            Cell cell = ExcelCellUtil.getCell(row, i);
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
    public static int getRowNum(Row row) {
        return row.getRowNum();
    }

}
