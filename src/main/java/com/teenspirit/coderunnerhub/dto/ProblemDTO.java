package com.teenspirit.coderunnerhub.dto;

import java.util.ArrayList;
import java.util.List;

public class ProblemDTO {

    private int appointmentId;
    private String language;
    private String code;
    private String functionName;
    private String returnType;
    private List<ArgumentDTO> arguments;

    public ProblemDTO() {
    }

    public ProblemDTO(int appointmentId, String language, String code, String functionName, String returnType, List<ArgumentDTO> arguments) {
        this.appointmentId = appointmentId;
        this.language = language;
        this.code = code;
        this.functionName = functionName;
        this.returnType = returnType;
        this.arguments = arguments;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
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

    public List<ArgumentDTO> getArguments() {
        return arguments;
    }

    public void setArguments(List<ArgumentDTO> arguments) {
        this.arguments = new ArrayList<>(arguments);
    }

    public static class ArgumentDTO {
        private String name;
        private String type;

        public ArgumentDTO() {

        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}