package com.teenspirit.coderunnerhub.containermanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Component
public class ContainerInitializer {

    private final ContainerPool containerPool;

    @Autowired
    public ContainerInitializer(ContainerPool containerPool) {

        this.containerPool = containerPool;
    }

    @PostConstruct
    public void initializeContainers() {
        int numberOfContainersToCreate = 3;

        for (int i = 0; i < numberOfContainersToCreate; i++) {
            Container container = containerPool.createContainer();
            containerPool.startContainer(container);
        }
    }
}