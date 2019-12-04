package com.excel.eom.util;

import com.excel.eom.annotation.ExcelObject;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;

public class HeaderUtil {

    public static CellStyle getHeaderCellStyle(Workbook book, Sheet sheet, Class<?> clazz) {
        CellStyle cellStyle = ExcelCellUtil.getCellStyle(book);
        if (clazz.isAnnotationPresent(ExcelObject.class)) {
            String name = clazz.getAnnotation(ExcelObject.class).name();
            IndexedColors cellColor = clazz.getAnnotation(ExcelObject.class).cellColor();
            short borderStyle = clazz.getAnnotation(ExcelObject.class).borderStyle();
            IndexedColors borderColor = clazz.getAnnotation(ExcelObject.class).borderColor();
            int fixedRowCount = clazz.getAnnotation(ExcelObject.class).fixedRowCount();
            int fixedColumnCount = clazz.getAnnotation(ExcelObject.class).fixedColumnCount();

            XSSFFont font = (XSSFFont) ExcelCellUtil.getFont(book);

            // style - 정적 (static) 영역은 여기에
            font.setBold(true);
            ExcelSheetUtil.createFreezePane(sheet, fixedColumnCount, fixedRowCount);
            ExcelCellUtil.setAlignment(cellStyle, CellStyle.ALIGN_CENTER);
            ExcelCellUtil.setFont(cellStyle, font);

            // style - 동적 (dynamic) 영역은 여기에
            ExcelCellUtil.setCellColor(cellStyle, cellColor);
            ExcelCellUtil.setCellBorder(cellStyle, borderStyle, borderColor);
        }
        return cellStyle;
    }
}
