package com.excel.eom.exception.body;

import com.excel.eom.exception.EOMRuntimeException;
import lombok.Getter;

import java.util.List;

public class EOMCellException extends EOMRuntimeException {

    @Getter
    public final int code;

    @Getter
    public final int row;

    @Getter
    public final int column;
    public String[] args;

    public EOMCellException(String message, int code, int row, int column, String... args) {
        super(message);
        this.code = code;
        this.row = row;
        this.column = column;
        this.args = args;
    }
}
