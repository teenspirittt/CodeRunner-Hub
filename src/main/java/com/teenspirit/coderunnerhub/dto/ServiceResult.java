package com.teenspirit.coderunnerhub.dto;

public class ServiceResult<T> {
    private final T data;
    private final boolean updated;

    public ServiceResult(T data, boolean updated) {
        this.data = data;
        this.updated = updated;
    }

    public T getData() {
        return data;
    }

    public boolean isUpdated() {
        return updated;
    }

}
