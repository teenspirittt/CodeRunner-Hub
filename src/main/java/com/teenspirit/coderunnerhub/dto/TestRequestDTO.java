package com.teenspirit.coderunnerhub.dto;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Data
public class TestRequestDTO implements Serializable {
    private int testPassed;
    private int totalTests;
    private int id;
    private int hashCode;
    private String output;


    public TestRequestDTO(){

    }

    public TestRequestDTO(int id, int hashCode){
        this.id = id;
        this.hashCode = hashCode;
    }

    public TestRequestDTO(int testPassed, int totalTests, int id) {
        this.testPassed = testPassed;
        this.totalTests = totalTests;
        this.id = id;
    }

    public void setTestPassed(int testPassed) {
        this.testPassed = testPassed;
    }

    public void setTotalTests(int totalTests) {
        this.totalTests = totalTests;
    }

    public boolean isPassed() {
        return testPassed == totalTests;
    }

    public boolean isError() {
        return this.output != null;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setHashCode(int hashCode) {
        this.hashCode = hashCode;
    }

    @Override
    public String toString() {
        return "id={" + id + "}";
    }

    public void incrementTestPassed(){
        testPassed++;
    }
}
