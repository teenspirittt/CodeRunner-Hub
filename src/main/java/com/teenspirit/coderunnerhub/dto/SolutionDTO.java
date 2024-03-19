package com.teenspirit.coderunnerhub.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class SolutionDTO {

    private int appointmentId;
    private String language;
    private String code;
    private String functionName;
    private String returnType;
    private List<ArgumentDTO> arguments;

    public SolutionDTO() {
    }

    public SolutionDTO(int appointmentId, String language, String code, String functionName, String returnType, List<ArgumentDTO> arguments) {
        this.appointmentId = appointmentId;
        this.language = language;
        this.code = code;
        this.functionName = functionName;
        this.returnType = returnType;
        this.arguments = arguments;
    }

    @Getter
    @Setter
    @ToString
    public static class ArgumentDTO {
        public ArgumentDTO(String type, String name) {
            this.name = name;
            this.type = type;
        }

        private String name;
        private String type;

        public ArgumentDTO() {

        }
    }
}