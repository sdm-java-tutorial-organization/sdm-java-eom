package com.excel.eom.exception;

import lombok.Data;

@Data
public class EOMRuntimeException extends RuntimeException {

    public String message;

    public EOMRuntimeException() {
        super();
    }

    public EOMRuntimeException(Throwable cause) {
        super(cause);
    }

}
