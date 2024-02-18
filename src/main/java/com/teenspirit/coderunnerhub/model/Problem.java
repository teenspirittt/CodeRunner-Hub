package com.teenspirit.coderunnerhub.model;


import com.teenspirit.coderunnerhub.dto.ProblemDTO;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

//todo remove dto from model

@Getter
@Document(collection = "problems")
public class Problem {

    @Id
    private int appointmentId;

    private String language;
    private String code;
    private String functionName;
    private String returnType;
    private List<ProblemDTO.ArgumentDTO> arguments;

    public void setLanguage(String programmingLanguage) {
        this.language = programmingLanguage;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
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


    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }
}
