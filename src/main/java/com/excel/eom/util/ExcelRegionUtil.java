package com.excel.eom.util;

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
}
