package com.teenspirit.coderunnerhub.model;


public record ExecutionResult(String result, String output, String error) {

    public boolean isError() {
        return this.error != null;
    }

}
