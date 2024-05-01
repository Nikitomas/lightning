package com.exception;

public class FrameworkException extends Exception {

    private static final long serialVersionUID = 689088837052178789L;

    /**
     * Constructs a UIException Object via message
     * @param message:String
     */
    public FrameworkException(String message) {
        super(message);
    }

    /**
     * Constructs a UIException Object via message and cause
     * @param message:String
     * @param cause:Throwable
     */
    public FrameworkException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a UIException Object via cause
     * @param cause:Exception
     */
    public FrameworkException(Exception cause) {
        super(cause);
    }

}
