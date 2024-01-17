package com.teenspirit.coderunnerhub.dto;

public record ServiceResult<T>(T data, boolean updated) {}
