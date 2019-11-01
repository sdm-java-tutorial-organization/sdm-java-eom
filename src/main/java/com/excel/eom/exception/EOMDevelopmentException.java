package com.excel.eom.exception;

public abstract class EOMDevelopmentException extends EOMRuntimeException {

    public static final String TYPE = "DEV";

    public EOMDevelopmentException() {
        super();
    }

    public EOMDevelopmentException(String message) {
        super(message);
    }

    public EOMDevelopmentException(String message, Throwable cause) {
        super(message, cause);
    }

    public EOMDevelopmentException(Throwable cause) {
        super(cause);
    }

}
