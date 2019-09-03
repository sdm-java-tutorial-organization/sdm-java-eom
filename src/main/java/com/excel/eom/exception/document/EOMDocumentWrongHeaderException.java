package com.excel.eom.exception.document;

import com.excel.eom.exception.EOMRuntimeException;
import lombok.Data;

@Data
public class EOMDocumentWrongHeaderException extends EOMRuntimeException {

    public String expectedName;
    public String responsedName;

    public EOMDocumentWrongHeaderException() {
        super();
    }

    public EOMDocumentWrongHeaderException(Throwable e) {
        super(e);
    }

    public EOMDocumentWrongHeaderException(Throwable e, String expectedName, String responsedName) {
        super(e);
        this.expectedName = expectedName;
        this.responsedName = responsedName;
    }

    public String getMessage() {
        return String.format("Wrong Header (expected name : %s, responsed name : %s)", expectedName, responsedName);
    }

}
