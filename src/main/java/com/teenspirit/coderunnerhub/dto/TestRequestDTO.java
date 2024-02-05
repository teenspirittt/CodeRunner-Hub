package com.teenspirit.coderunnerhub.dto;

import jdk.incubator.vector.VectorOperators;

public class TestRequestDTO {
    private int testPassed;
    private int totalTests;
    // private final int time;

    public TestRequestDTO(int testPassed, int totalTests) {
        this.testPassed = testPassed;
        this.totalTests = totalTests;
    }

    public int getTestPassed() {
        return testPassed;
    }

    public void setTestPassed(int testPassed) {
        this.testPassed = testPassed;
    }

    public int getTotalTests() {
        return totalTests;
    }

    public void setTotalTests(int totalTests) {
        this.totalTests = totalTests;
    }

    public boolean isPassed() {
        return testPassed == totalTests;
    }
}
