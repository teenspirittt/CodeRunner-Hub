package com.teenspirit.coderunnerhub.model;

public class ExecuteResponse {
    private boolean success;
    private String message;
    private String output;
    private String error;

    public ExecuteResponse(boolean success, String message, String output, String error) {
        this.success = success;
        this.message = message;
        this.output = output;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
