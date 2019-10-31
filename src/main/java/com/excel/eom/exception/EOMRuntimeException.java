package com.excel.eom.exception;

public class EOMRuntimeException extends RuntimeException {

    public EOMRuntimeException() {
        super();
    }

    public EOMRuntimeException(String message) {
        super(message);
    }

    public EOMRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public EOMRuntimeException(Throwable cause) {
        super(cause);
    }

}
