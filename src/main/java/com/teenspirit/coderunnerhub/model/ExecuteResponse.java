package com.teenspirit.coderunnerhub.model;

public record ExecuteResponse(
        boolean success,
        String message,
        String output,
        String error,
        int testsPassed,
        int totalTests
) {

}
