package com.teenspirit.coderunnerhub.model;

public class AssignmentData {
    private int appointmentId;
    private String code;
    private String language;

    // Конструкторы, геттеры и сеттеры

    public AssignmentData() {
    }

    public AssignmentData(int appointmentId, String code, String language) {
        this.appointmentId = appointmentId;
        this.code = code;
        this.language = language;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
