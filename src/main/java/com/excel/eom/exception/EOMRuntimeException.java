package com.excel.eom.exception;

import lombok.Data;

@Data
public class EOMRuntimeException extends RuntimeException {

    public EOMRuntimeException() {
        super();
    }

    public EOMRuntimeException(String message) {
        super(message);
    }

    public EOMRuntimeException(Throwable cause) {
        super(cause);
    }

}
