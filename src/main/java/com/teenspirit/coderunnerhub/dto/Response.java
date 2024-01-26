package com.teenspirit.coderunnerhub.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.http.HttpStatus;


public record Response<T>(int status, String message, String error, T data) {

    public static <T> Response<T> create(HttpStatus httpStatus, T data, String error) {
        return new Response<>(httpStatus.value(), httpStatus.getReasonPhrase(), error, data);
    }

    public static <T> Response<T> ok(T data) {
        return create(HttpStatus.OK, data, null);
    }

    public static <T> Response<T> created(T data) {
        return create(HttpStatus.CREATED, data, null);
    }

    public static <T> Response<T> notFound(String error) {
        return create(HttpStatus.NOT_FOUND, null, error);
    }

    public static <T> Response<T> badRequest(String error) {
        return create(HttpStatus.BAD_REQUEST, null, error);
    }

    public static <T> Response<T> internalServerError(String error) {
        return create(HttpStatus.INTERNAL_SERVER_ERROR, null, error);
    }

    public static <T> Response<T> noContent() {
        return create(HttpStatus.NO_CONTENT, null, null);
    }

    public static <T> Response<T> createError(HttpStatus httpStatus, String error) {
        return create(httpStatus, null, error);
    }

    @JsonIgnore
    public boolean isSuccessful() {
        return HttpStatus.valueOf(status).is2xxSuccessful();
    }
}
