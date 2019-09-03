package com.excel.eom.tutorial;

import com.excel.eom.builder.deprecated.ExcelDocumentBuilder;
import com.excel.eom.builder.deprecated.ExcelObjectBuilder;
import com.excel.eom.constant.deprecated.OldExcelObject;
import com.excel.eom.tutorial.deprecated.OldExcelObjectDemo;
import com.excel.eom.tutorial.deprecated.MultipleParentColumnOld;

import java.util.ArrayList;
import java.util.List;

public class ClientObjectCreater {

    ExcelObjectBuilder<MultipleParentColumnOld, OldExcelObjectDemo> excelObjectBuilder =
            new ExcelObjectBuilder(MultipleParentColumnOld.class, OldExcelObjectDemo.class);

    ExcelDocumentBuilder<OldExcelObjectDemo> excelDocumentBuilder = new ExcelDocumentBuilder(OldExcelObjectDemo.class, MultipleParentColumnOld.class);

    /**
     * createExcelObjectDummy
     * List<List<String>> --> List<T>
     *
     * @param dummy
     * */
    public <T extends OldExcelObject> List<T> createExcelObjectDummy(List<List> dummy) {
        List<T> results = new ArrayList<>();
        for(List rowDummy : dummy) {
            Object object = excelDocumentBuilder.getExcelObjectByList(rowDummy);
            T t = (T) object;
            results.add(t);
        }
        return results;
    }

}
