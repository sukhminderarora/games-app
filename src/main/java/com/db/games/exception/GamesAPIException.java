package com.db.games.exception;

public class GamesAPIException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String error;
    private String detailedMessage;

    public GamesAPIException(String error, String detailedMessage) {
        super(detailedMessage);
        this.error = error;
        this.detailedMessage = detailedMessage;

    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getDetailedMessage() {
        return detailedMessage;
    }

    public void setDetailedMessage(String detailedMessage) {
        this.detailedMessage = detailedMessage;
    }

}
