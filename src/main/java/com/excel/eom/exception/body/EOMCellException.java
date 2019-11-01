package com.excel.eom.exception.body;

import com.excel.eom.exception.EOMRuntimeException;
import lombok.Getter;

public abstract class EOMCellException extends EOMRuntimeException {

    @Getter
    public int code;

    @Getter
    public int row;

    @Getter
    public int column;

    public String[] args;

    public EOMCellException() {
        super();
    }

    public EOMCellException(String message) {
        super(message);
    }

    public EOMCellException(String message, Throwable cause) {
        super(message, cause);
    }

    public EOMCellException(Throwable cause) {
        super(cause);
    }

    public EOMCellException(String message, int code, int row, int column, String... args) {
        this(message);
        this.code = code;
        this.row = row;
        this.column = column;
        this.args = args;
    }

    public EOMCellException(String message, Throwable cause, int code, int row, int column, String... args) {
        this(message, cause);
        this.code = code;
        this.row = row;
        this.column = column;
        this.args = args;
    }

}
