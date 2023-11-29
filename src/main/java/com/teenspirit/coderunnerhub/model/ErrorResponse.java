package com.teenspirit.coderunnerhub.model;

public class ErrorResponse {
    private String error;

    // Конструкторы, геттеры и сеттеры

    public ErrorResponse() {
    }

    public ErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}

