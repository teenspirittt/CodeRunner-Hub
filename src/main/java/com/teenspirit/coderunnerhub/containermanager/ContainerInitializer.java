package com.teenspirit.coderunnerhub.containermanager;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ContainerInitializer {

    private final ContainerPool containerPool;
    private static final Logger LOGGER = LoggerFactory.getLogger(ContainerInitializer.class);

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
            LOGGER.info("CONTAINER " + i + " CREATED");
        }
    }

    @PreDestroy
    public void destroyContainers() {
        for (Container container : containerPool.getPool().values()) {
            containerPool.removeContainer(container);
        }
    }

}
