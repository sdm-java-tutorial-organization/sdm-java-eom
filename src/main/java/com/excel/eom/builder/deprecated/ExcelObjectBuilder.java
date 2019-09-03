package com.excel.eom.builder.deprecated;

import com.excel.eom.constant.deprecated.OldExcelColumn;
import com.excel.eom.constant.deprecated.SheetContext;
import com.excel.eom.exception.document.EOMNoDocumentException;
import com.excel.eom.exception.object.EOMNoObjectException;
import com.excel.eom.exception.object.EOMObjectWrongDataTypeException;
import com.excel.eom.constant.deprecated.OldExcelObject;
import com.excel.eom.constant.SheetConstant;
import com.excel.eom.exception.object.EOMObjectNotFoundFieldException;
import com.excel.eom.model.deprecated.HeaderElement;
import com.excel.eom.util.ExcelRegionUtil;
import com.excel.eom.util.ExcelRowUtil;
import com.excel.eom.util.deprecated.HeaderElementUtil;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.*;
import java.util.stream.Collectors;

@Deprecated
public class ExcelObjectBuilder<T extends OldExcelColumn, V extends OldExcelObject> {

    Class<T> columnType;
    Class<V> objectType;
    T[] columns;
    List<HeaderElement> headerElements = new ArrayList<>();

    public ExcelObjectBuilder(List<HeaderElement> headerElements,
                              Class<V> objectType)
            throws EOMNoDocumentException, EOMNoObjectException, EOMObjectWrongDataTypeException, EOMObjectNotFoundFieldException {

        // TODO runtime -> compile
        /*ExcelColumnValidator.checkType(columnType);
        ExcelObjectValidator.checkType(objectType);
        CompareValidator.compare(columnType, objectType);*/

        this.headerElements = headerElements;
        this.objectType = objectType;

    }

    public ExcelObjectBuilder(Class<T> columnType,
                              Class<V> objectType)
            throws EOMNoDocumentException, EOMNoObjectException, EOMObjectWrongDataTypeException, EOMObjectNotFoundFieldException {

        // TODO runtime -> compile
        /*ExcelColumnValidator.checkType(columnType);
        ExcelObjectValidator.checkType(objectType);
        CompareValidator.compare(columnType, objectType);*/

        this.columns = columnType.getEnumConstants();
        HeaderElementUtil.parseColumnToElement(this.headerElements, this.columns, HeaderElement.DEFAULT_LEVEL);
        this.columnType = columnType;
        this.objectType = objectType;

    }

    /**
     * build
     *
     * @param sheet
     * @param objects
     */
    public void build(XSSFSheet sheet,
                      List<V> objects)
            throws EOMObjectNotFoundFieldException {
        SheetContext sheetContext = new SheetContext(sheet, headerElements);

        // header
        sheetContext.setRow(ExcelRowUtil.initRow(sheet, SheetConstant.HEADER_ROW));
        setHeaderRow(sheetContext);

        // body
        if (objects != null && objects.size() > 0) {
            for (int i = 0; i < objects.size(); i++) {
                V object = objects.get(i);
                sheetContext.setRow(ExcelRowUtil.initRow(sheet, i + SheetConstant.BODY_START_ROW));
                setBodyRow(sheetContext, object);
            }
        }

        // region
        for(HeaderElement element : sheetContext.getElements()) {
            if(element.getLevel() < HeaderElementUtil.getTopLevel(headerElements)) {
                ExcelRegionUtil.setColumnRegion(sheetContext, element, objects);
            }
        }
    }


    /**
     * setHeaderRow
     *
     * @param sheetContext
     */
    private void setHeaderRow(SheetContext sheetContext) {
        List<HeaderElement> elements = sheetContext.getElements();
        XSSFRow row = sheetContext.getRow();
        List<String> names = elements.stream()
                .map(element -> element.getName())
                .collect(Collectors.toList());
        ExcelRowUtil.setRowValue(row, names);
    }

    /**
     * setBodyRow
     *
     * @param sheetContext
     * @param excelObject
     */
    private void setBodyRow(SheetContext sheetContext,
                            V excelObject)
            throws EOMObjectNotFoundFieldException {
        List<HeaderElement> elements = sheetContext.getElements();
        XSSFRow row = sheetContext.getRow();
        List<String> values = new ArrayList<>();
        for (HeaderElement headerElement : elements) {
            String value = excelObject.getFieldValue(headerElement.getField());
            values.add(value);
        }
        ExcelRowUtil.setRowValue(row, values);
    }

}
