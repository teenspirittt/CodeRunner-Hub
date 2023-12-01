package com.teenspirit.coderunnerhub.model;


import com.teenspirit.coderunnerhub.dto.ProblemDTO;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "problems")
public class Problem {

    @Id
    private int appointmentId;

    private String language;
    private String code;
    private String functionName;
    private String returnType;
    private List<ProblemDTO.ArgumentDTO> arguments;

    public String getProgrammingLanguage() {
        return language;
    }

    public void setProgrammingLanguage(String programmingLanguage) {
        this.language = programmingLanguage;
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

    public List<ProblemDTO.ArgumentDTO> getArguments() {
        return arguments;
    }

    public void setArguments(List<ProblemDTO.ArgumentDTO> arguments) {
        this.arguments = arguments;
    }


    public Problem() {
    }

    public Problem(int appointmentId, String programmingLanguage, String code, String functionName, String returnType, List<ProblemDTO.ArgumentDTO> arguments) {
        this.appointmentId = appointmentId;
        this.language = programmingLanguage;
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
}
