package com.excel.eom.builder.deprecated;

import com.excel.eom.constant.deprecated.OldExcelObject;
import com.excel.eom.constant.deprecated.SheetContext;
import com.excel.eom.model.deprecated.HeaderElement;
import com.excel.eom.exception.document.EOMDocumentWrongDataTypeException;
import com.excel.eom.exception.document.EOMNoDocumentException;
import com.excel.eom.exception.object.EOMNoObjectException;
import com.excel.eom.exception.object.EOMObjectWrongDataTypeException;
import com.excel.eom.constant.deprecated.OldExcelColumn;
import com.excel.eom.constant.SheetConstant;
import com.excel.eom.exception.document.EOMDocumentWrongHeaderException;
import com.excel.eom.exception.object.EOMObjectNotFoundFieldException;
import com.excel.eom.factory.ExcelObjectFactory;
import com.excel.eom.util.*;
import com.excel.eom.util.deprecated.HeaderElementUtil;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Deprecated
public class ExcelDocumentBuilder<V extends OldExcelObject> {

    Class<V> objectType;

    List<HeaderElement> headerElements = new ArrayList<>();

    public ExcelDocumentBuilder(Class<V> objectType, List<HeaderElement> headerElements)
            throws EOMNoDocumentException, EOMNoObjectException, EOMObjectWrongDataTypeException, EOMObjectNotFoundFieldException {

        // TODO runtime -> compile
        /*ExcelColumnValidator.checkType(columnType);
        ExcelObjectValidator.checkType(objectType);
        CompareValidator.compare(columnType, objectType);*/

        this.objectType = objectType;
        this.headerElements = headerElements;
    }

    public ExcelDocumentBuilder(Class<V> objectType, Class<?> columnType)
            throws EOMNoDocumentException, EOMNoObjectException, EOMObjectWrongDataTypeException, EOMObjectNotFoundFieldException {

        // TODO runtime -> compile
        /*ExcelColumnValidator.checkType(columnType);
        ExcelObjectValidator.checkType(objectType);
        CompareValidator.compare(columnType, objectType);*/

        this.objectType = objectType;
        OldExcelColumn[] columns = (OldExcelColumn[]) columnType.getEnumConstants();
        HeaderElementUtil.parseColumnToElement(this.headerElements, columns, HeaderElement.DEFAULT_LEVEL);

    }

    /**
     * build
     *
     * @param sheet
     * @param height
     */
    public List<V> build(XSSFSheet sheet,
                         int height)
            throws EOMDocumentWrongHeaderException, EOMNoObjectException, EOMObjectNotFoundFieldException, EOMDocumentWrongDataTypeException {
        SheetContext sheetContext = new SheetContext(sheet, headerElements);
        List<V> result = new ArrayList<>(height);

        // header
        sheetContext.setRow(ExcelRowUtil.getRow(sheet, SheetConstant.HEADER_ROW));
        validHeader(sheetContext);

        // body
        List<String> listOfCellValue;
        sheetContext.setTopLevel(HeaderElementUtil.getTopLevel(headerElements));
        Map<HeaderElement, String> mapOfPreValue = HeaderElementUtil.getMapOfPreValue(sheetContext);

        for (int rowIdx = SheetConstant.BODY_START_ROW; rowIdx < height; rowIdx++) {

            // Row -> List (With MergedArea Value)
            sheetContext.setRowIdx(rowIdx);
            sheetContext.setRow(ExcelRowUtil.getRow(sheet, rowIdx));
            listOfCellValue = convertRowToListWithMergedAreaValue(sheetContext, mapOfPreValue);

            V obj;
            try {
                obj = getExcelObjectByValues(sheetContext, listOfCellValue);
            } catch (EOMDocumentWrongDataTypeException e) {
                e.row = rowIdx;
                throw e;
            }
            result.add(obj);
        }
        return result;
    }

    /**
     * validHeader
     *
     * @param sheetContext
     */
    public boolean validHeader(SheetContext sheetContext)
            throws EOMDocumentWrongHeaderException {
        List<HeaderElement> elements = sheetContext.getElements();
        XSSFRow row = sheetContext.getRow();
        for (HeaderElement headerElement : elements) {
            XSSFCell cell = ExcelCellUtil.getCell(row, headerElement.getOrder());
            String expectedName = headerElement.getName();
            if (cell != null) {
                Object value = ExcelCellUtil.getCellValue(/*column.getType(), */cell);
                String responsedName = (String) value;
                if (headerElement.getName().equals(value) == false) {
                    EOMDocumentWrongHeaderException eomDocumentWrongHeaderException =
                            new EOMDocumentWrongHeaderException(null, expectedName, responsedName);
                    throw eomDocumentWrongHeaderException;
                }
            } else {
                EOMDocumentWrongHeaderException eomDocumentWrongHeaderException =
                        new EOMDocumentWrongHeaderException(null, expectedName, null);
                throw eomDocumentWrongHeaderException;
            }
        }
        return true;
    }

    /**
     * convertRowToListWithMergedAreaValue
     *
     * @param sheetContext
     * @param mapOfPreValue
     */
    public List<String> convertRowToListWithMergedAreaValue(SheetContext sheetContext,
                                                            Map<HeaderElement, String> mapOfPreValue) {
        List listOfCellValue = new ArrayList<>();
        XSSFSheet sheet = sheetContext.getSheet();
        XSSFRow row = sheetContext.getRow();
        int topLevel = sheetContext.getTopLevel();
        int rowIdx = sheetContext.getRowIdx();

        for (HeaderElement headerElement : headerElements) {
            int columnIdx = headerElement.getOrder();
            XSSFCell cell = ExcelCellUtil.getCell(row, columnIdx);
            String value;
            if (headerElement.getLevel() < topLevel) {
                CellRangeAddress area = ExcelSheetUtil.getMergedRegion(sheet, rowIdx, columnIdx);
                value = HeaderElementUtil.setParentLevelElement(mapOfPreValue, headerElement, area, cell);
            } else {
                value = (String) ExcelCellUtil.getCellValue(cell);
            }
            listOfCellValue.add(value);
        }
        return listOfCellValue;
    }

    /**
     * getExcelObjectByValues
     *
     * @param sheetContext
     * @param listOfCell
     */
    public V getExcelObjectByValues(SheetContext sheetContext,
                                    List<String> listOfCell)
            throws EOMNoObjectException, EOMObjectNotFoundFieldException, EOMDocumentWrongDataTypeException {
        V obj = ExcelObjectFactory.createExcelObject(objectType);
        List<HeaderElement> elements = sheetContext.getElements();

        for (HeaderElement column : elements) {
            int columnIdx = column.getOrder();
            try {
                obj.build(column.getType(), column.getField(), listOfCell.get(columnIdx));
            } catch (EOMDocumentWrongDataTypeException e) {
                e.column = columnIdx;
                throw e;
            }
        }
        return obj;
    }

    /**
     * getExcelObjectByList
     *
     * @param listOfRow
     * @refer objectType
     * @refer headerElements
     */
    @Deprecated
    public V getExcelObjectByList(List listOfRow)
            throws EOMNoObjectException, EOMObjectNotFoundFieldException, EOMDocumentWrongDataTypeException {
        V obj = ExcelObjectFactory.createExcelObject(objectType);
        for (HeaderElement element : headerElements) {
            int columnIdx = element.getOrder();
            Object value = listOfRow.get(columnIdx);
            try {
                obj.build(element.getType(), element.getField(), value);
            } catch (EOMDocumentWrongDataTypeException e) {
                e.column = columnIdx;
                throw e;
            }
        }
        return obj;
    }

}
