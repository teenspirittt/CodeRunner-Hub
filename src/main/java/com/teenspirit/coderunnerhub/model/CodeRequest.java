package com.teenspirit.coderunnerhub.model;

import com.teenspirit.coderunnerhub.dto.SolutionDTO;
import lombok.Getter;

import java.util.List;

@Getter
public class CodeRequest {
    private String code;
    private String funcName;
    private String returnType;
    private List<SolutionDTO.ArgumentDTO> arguments;


    public CodeRequest(String code, String funcName, String returnType, List<SolutionDTO.ArgumentDTO> arguments) {
        this.code = code;
        this.funcName = funcName;
        this.returnType = returnType;
        this.arguments = arguments;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public void setArguments(List<SolutionDTO.ArgumentDTO> arguments) {
        this.arguments = arguments;
    }
}

