package com.teenspirit.coderunnerhub.containermanager;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Container {

    private final String id;
    private final String imageName;
    private final String sourceCode;
    private final String workingDirectory;

    private int activeUsages;

    public Container(String id, String imageName, String sourceCode, String workingDirectory) {
        this.id = id;
        this.imageName = imageName;
        this.sourceCode = sourceCode;
        this.workingDirectory = workingDirectory;
        this.activeUsages = 0;
    }

    public synchronized void incrementActiveUsages() {
        activeUsages++;
    }

    public synchronized void decrementActiveUsages() {
        activeUsages--;
    }

    public synchronized boolean isAvailable() {
        return activeUsages == 0;
    }
}
