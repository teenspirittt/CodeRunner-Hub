package com.teenspirit.coderunnerhub.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveSolutionDTO {

    private int appointmentId;
    private String language;
    private String code;

    private String funcName;

}
