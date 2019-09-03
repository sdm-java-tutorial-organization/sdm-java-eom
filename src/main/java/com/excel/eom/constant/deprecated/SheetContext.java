package com.excel.eom.constant.deprecated;

import com.excel.eom.model.deprecated.HeaderElement;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.List;

@Deprecated
public class SheetContext {

    @Getter
    private XSSFSheet sheet;

    @Getter
    private List<HeaderElement> elements;

    @Getter
    @Setter
    public XSSFRow row;

    @Getter
    @Setter
    public int rowIdx;

    @Getter
    @Setter
    public int columnIdx;

    @Getter
    @Setter
    public int topLevel;

    public SheetContext(XSSFSheet sheet,
                        List<HeaderElement> elements) {
        this.sheet = sheet;
        this.elements = elements;
    }
}
