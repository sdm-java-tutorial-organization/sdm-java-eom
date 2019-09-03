package com.excel.eom.exception.document;

import com.excel.eom.exception.EOMRuntimeException;
import lombok.Data;

/**
 * Wrong OldExcelObject
 *
 * - InstantiationException (추상구현체)
 * - IllegalAccessException (접근범위)
 * */
@Data
public class EOMNoDocumentException extends EOMRuntimeException {

    public String enumName;

    public EOMNoDocumentException() {
        super();
    }

    public EOMNoDocumentException(Throwable e, String enumName) {
        super(e);
        this.enumName = enumName;
    }

    public String getMessage() {
        return String.format("Wrong OldExcelColumn (Class : %s)", enumName);
    }
}
