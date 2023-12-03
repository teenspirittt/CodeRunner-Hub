package com.teenspirit.coderunnerhub.model;

import com.teenspirit.coderunnerhub.dto.ProblemDTO;

import java.util.List;

public class CodeRequest {
    private String code;
    private String funcName;
    private String returnType;
    private List<ProblemDTO.ArgumentDTO> arguments;


    public CodeRequest(String code, String funcName, String returnType, List<ProblemDTO.ArgumentDTO> arguments) {
        this.code = code;
        this.funcName = funcName;
        this.returnType = returnType;
        this.arguments = arguments;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public List<ProblemDTO.ArgumentDTO> getArguments() {
        return arguments;
    }

    public void setArguments(List<ProblemDTO.ArgumentDTO> arguments) {
        this.arguments = arguments;
    }
}

