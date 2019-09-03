package com.excel.eom.exception.document;

import com.excel.eom.exception.EOMRuntimeException;
import lombok.Data;

/**
 * Wrong DataType when create excelObject
 *
 * - NumberFormatException
 * - IllegalArgumentException
 * */
@Data
public class EOMDocumentWrongDataTypeException extends EOMRuntimeException {

    public String requiredType = "";
    public int row = -1;
    public int column = -1;

    public EOMDocumentWrongDataTypeException() {
        super();
    }

    public EOMDocumentWrongDataTypeException(Throwable e) {
        super(e);
    }

    public EOMDocumentWrongDataTypeException(Throwable e, String requiredType) {
        super(e);
        this.requiredType = requiredType;
    }

    public String getMessage() {
        return String.format("%s Wrong DataType (required type : %s, row : %s, column : %s)", message, requiredType, row, column);
    }


}
