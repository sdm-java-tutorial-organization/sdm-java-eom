package com.excel.eom.util;

import com.excel.eom.constant.deprecated.OldExcelObject;
import com.excel.eom.constant.deprecated.SheetContext;
import com.excel.eom.model.deprecated.HeaderElement;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ExcelRegionUtil {

    private static final Logger logger = LoggerFactory.getLogger(ExcelRegionUtil.class);

    /**
     * print
     *
     * @param sheet
     * */
    public static void print(XSSFSheet sheet) {
        for (int i = 0; i < sheet.getNumMergedRegions(); ++i) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            logger.debug(String.format("fr : %d, lr : %d, fc : %d, lc : %d",
                    range.getFirstRow(),
                    range.getLastRow(),
                    range.getFirstColumn(),
                    range.getLastColumn()));
        }
    }

    /**
     * setColumnRegion
     *
     * @param sheetContext
     * @param element
     * @param objects
     */
    @Deprecated
    public static <V extends OldExcelObject> void setColumnRegion(SheetContext sheetContext,
                                                                  HeaderElement element,
                                                                  List<V> objects) {
        String preValue = "";
        int firstRowRegion = 1;
        int columnIdx = element.getOrder();

        for (int rowIdx = 1; rowIdx < objects.size(); rowIdx++) {
            V object = objects.get(rowIdx - 1);
            String field = element.getField();
            String value = object.getFieldValue(field);

            if (preValue == null || preValue.equals("")) {
                preValue = value;
            } else {
                if (preValue.equals(value) == false) {
                    ExcelSheetUtil.addRegion(sheetContext.getSheet(),
                            firstRowRegion, rowIdx - 1,
                            columnIdx, columnIdx);
                    firstRowRegion = rowIdx;
                    preValue = value;
                }
            }
        }

        // last region
        ExcelSheetUtil.addRegion(sheetContext.getSheet(),
                firstRowRegion, objects.size(),
                columnIdx, columnIdx);
    }
}
