package com.excel.eom.util.deprecated;

import com.excel.eom.constant.deprecated.OldExcelColumn;
import com.excel.eom.constant.deprecated.OldExcelObject;
import com.excel.eom.constant.deprecated.SheetContext;
import com.excel.eom.model.deprecated.HeaderElement;
import com.excel.eom.util.ExcelCellUtil;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
public class HeaderElementUtil {

    /**
     * parseColumnToElement
     *
     * @param headerElements
     * @param columns
     * @param currentLevel
     */
    public static List<HeaderElement> parseColumnToElement(List<HeaderElement> headerElements,
                                                           OldExcelColumn[] columns,
                                                           int currentLevel) {
        for (OldExcelColumn column : columns) {
            Class<?> dataType = column.getType();
            if (OldExcelColumn.class.isAssignableFrom(dataType)) {
                OldExcelColumn[] subColumns = (OldExcelColumn[]) dataType.getEnumConstants();
                parseColumnToElement(headerElements, subColumns, ++currentLevel);
            } else {
                headerElements.add(new HeaderElement(
                        column.toString(), column.getOrder(), column.getField(), column.getType(), currentLevel
                ));
            }
        }
        return headerElements;
    }

    /**
     * getTopLevel
     *
     * @param headerElements
     */
    public static int getTopLevel(List<HeaderElement> headerElements) {
        return headerElements.stream()
                .map(element -> element.getLevel())
                .max((a, b) -> a - b)
                .get();
    }

    /**
     * setParentLevelElement
     *
     * @param mapOfPreValue
     * @param headerElement
     * @param area
     * @param cell
     */
    public static String setParentLevelElement(Map<HeaderElement, String> mapOfPreValue,
                                               HeaderElement headerElement,
                                               CellRangeAddress area,
                                               XSSFCell cell) {
        String result;
        if (headerElement.compareArea(area)) {
            result = mapOfPreValue.get(headerElement);
        } else {
            result = (String) ExcelCellUtil.getCellValue(cell);
            headerElement.setArea(area);
            mapOfPreValue.put(headerElement, result);
        }
        return result;
    }

    /**
     * getMapOfRegionEndpoint : `merged area` 에서 이전 인덱스를 저장하기 위해 사용하는 Map
     *
     * @param sheetContext
     */
    public static Map<HeaderElement, Integer> getMapOfRegionEndpoint(SheetContext sheetContext) {
        Map<HeaderElement, Integer> mapOfRegionEndPoint = new HashMap<>();
        List<HeaderElement> elements = sheetContext.getElements();
        int topLevel = sheetContext.getTopLevel();
        for (HeaderElement headerElement : elements) {
            if (headerElement.getLevel() < topLevel) {
                mapOfRegionEndPoint.put(headerElement, 1);
            }
        }
        return mapOfRegionEndPoint;
    }

    /**
     * getMapOfPreValue : `merged area` 에서 이전 값을 저장하기 위해 사용하는 Map
     *
     * @param sheetContext
     */
    @Deprecated
    public static <T extends OldExcelObject> Map<HeaderElement, String> getMapOfPreValue(SheetContext sheetContext) {
        Map<HeaderElement, String> mapOfPreValue = new HashMap<>();
        List<HeaderElement> elements = sheetContext.getElements();
        int topLevel = sheetContext.getTopLevel();
        for (HeaderElement headerElement : elements) {
            if (headerElement.getLevel() < topLevel) {
                mapOfPreValue.put(headerElement, "");
            }
        }
        return mapOfPreValue;
    }

}
