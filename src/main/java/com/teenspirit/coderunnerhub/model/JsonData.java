package com.teenspirit.coderunnerhub.model;

public class JsonData {
    private int appointmentId;
    private String language;
    private String code;
    private String funcName;


    public JsonData() {
    }

    public JsonData(int appointmentId, String language, String code, String funcName) {
        this.appointmentId = appointmentId;
        this.language = language;
        this.code = code;
        this.funcName = funcName;
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

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }
}
