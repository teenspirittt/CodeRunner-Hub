package com.teenspirit.coderunnerhub.model;

import lombok.Getter;

@Getter
public record ExecutionResult(String result, String error) {

    public boolean isError() {
        return this.error != null;
    }

}
