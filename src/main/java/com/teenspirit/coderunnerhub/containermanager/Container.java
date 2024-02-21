package com.teenspirit.coderunnerhub.containermanager;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Container {

    private final String id;
    private final String imageName;
    private int totalActiveUsages;
    private int activeUsages;

    public Container(String id, String imageName) {
        this.id = id;
        this.imageName = imageName;
        this.activeUsages = 0;
        this.totalActiveUsages = 5;
    }

    public synchronized void incrementActiveUsages() {
        activeUsages++;
    }

    public synchronized void decrementActiveUsages() {
        activeUsages--;
    }

    public synchronized boolean isAvailable() {
        return activeUsages <  totalActiveUsages;
    }
}
