package com.db.games.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ErrorResponse {

    private int status;
    private String error;

    public ErrorResponse(int status, String error) {
        this.status = status;
        this.error = error;
    }
}