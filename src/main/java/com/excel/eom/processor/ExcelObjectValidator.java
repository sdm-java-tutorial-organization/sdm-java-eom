package com.excel.eom.processor;

import com.excel.eom.exception.object.EOMNoObjectException;
import com.excel.eom.constant.deprecated.OldExcelObject;
import com.excel.eom.exception.document.EOMNoDocumentException;

@Deprecated
public class ExcelObjectValidator {

    /**
     * checkExcelObject
     *
     * @param objectType
     * */
    public static <T extends OldExcelObject> void checkType(Class<T> objectType)
            throws EOMNoDocumentException {
        if(OldExcelObject.isExcelObject(objectType) == false) {
            throw new EOMNoObjectException(null, objectType.getSimpleName());
        }
    }
}
