package com.yonyou.uap.studio.connection.exception;

public class ConnectionException extends Exception {
    public ConnectionException() {
    }

    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectionException(String message) {
        super(message);
    }

    public ConnectionException(Throwable cause) {
        super(cause);
    }
}
