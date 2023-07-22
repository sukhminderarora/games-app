package com.db.games.exception;

import org.springframework.http.HttpStatus;

public class GameDataException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String message;

    private Exception exception;

    private HttpStatus status;

    public GameDataException(Exception e) {
        super(e);
    }

    public GameDataException(String message, Exception e) {
        super(message, e);
        this.message = message;
        this.exception = e;
    }

    public GameDataException(String message, Exception e, HttpStatus status) {
        super(message, e);
        this.message = message;
        this.exception = e;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

}
