package com.excel.eom.factory;

import com.excel.eom.constant.deprecated.OldExcelObject;
import com.excel.eom.exception.object.EOMNoObjectException;

@Deprecated
public class ExcelObjectFactory {

    /**
     * createExcelObject
     *
     * @param clazz
     * */
    public static <T extends OldExcelObject> T createExcelObject(Class<?> clazz)
            throws EOMNoObjectException {
        T obj;
        try {
            obj = (T) clazz.newInstance();
        } catch (InstantiationException e) {
            throw new EOMNoObjectException(e, clazz.getSimpleName());
        } catch (IllegalAccessException e) {
            throw new EOMNoObjectException(e, clazz.getSimpleName());
        }
        return obj;
    }
}
