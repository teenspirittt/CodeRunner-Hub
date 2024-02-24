package com.teenspirit.coderunnerhub.containermanager;


import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.transport.DockerHttpClient;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;




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
            System.out.println("HELLO " + i);
        }
    }

    @PreDestroy
    public void destroyContainers() {
        for (Container container : containerPool.getPool().values()) {
            containerPool.removeContainer(container);
        }
    }

}
