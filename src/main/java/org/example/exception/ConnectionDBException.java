package org.example.exception;

public class ConnectionDBException extends Exception {
    public ConnectionDBException(String errorMessage) {
        super(errorMessage);
    }

    public ConnectionDBException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }
}
