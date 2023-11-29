package com.teenspirit.coderunnerhub.model;

import java.util.List;

public class AssignmentCode {
    private String language;
    private String code;
    private String functionName;
    private String returnType;
    private List<String> arguments;

    // Конструкторы, геттеры и сеттеры

    public AssignmentCode() {
    }

    public AssignmentCode(String language, String code, String functionName, String returnType, List<String> arguments) {
        this.language = language;
        this.code = code;
        this.functionName = functionName;
        this.returnType = returnType;
        this.arguments = arguments;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }
}
