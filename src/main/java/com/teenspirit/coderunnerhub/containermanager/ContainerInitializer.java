package com.teenspirit.coderunnerhub.containermanager;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.teenspirit.coderunnerhub.exceptions.NotFoundException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class ContainerInitializer {

    private final ContainerPool containerPool;
    private final DockerClient dockerClient;
    private static final Logger LOGGER = LoggerFactory.getLogger(ContainerInitializer.class);

    @Autowired
    public ContainerInitializer(ContainerPool containerPool, DockerClient dockerClient) {

        this.containerPool = containerPool;
        this.dockerClient = dockerClient;
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

    @Scheduled(fixedDelay = 15000) // 15 seconds
    public void checkContainerHealth() {
        for (Container container : containerPool.getPool().values()) {
            String containerId = container.getId();
            try {
                InspectContainerResponse response = dockerClient.inspectContainerCmd(containerId).exec();
                if (Boolean.FALSE.equals(response.getState().getRunning())) {
                    LOGGER.warn("Container " + containerId + " is not running. Restoring...");
                    containerPool.removeContainer(container);
                    Container newContainer = containerPool.createContainer();
                    containerPool.startContainer(newContainer);
                    LOGGER.info("New container created: " + newContainer.getId());
                }
            } catch (NotFoundException e) {
                LOGGER.error("Container " + containerId + " not found. Removing from pool...");
                containerPool.removeContainer(container);
            } catch (Exception e) {
                LOGGER.error("Error checking container " + containerId + " health: " + e.getMessage());
            }
        }
    }

    @Scheduled(fixedDelay = 600000) // 10 minutes
    public void clearContainerContent() {
        for (Container container : containerPool.getPool().values()) {
            String containerId = container.getId();
            try {
                dockerClient
                        .execCreateCmd(containerId)
                        .withCmd("sh", "-c", "rm -rf /usr/src/app/*")
                        .exec();
                LOGGER.info("Content of container " + containerId + " cleared.");
            } catch (Exception e) {
                LOGGER.error("Error clearing content of container " + containerId + ": " + e.getMessage());
            }
        }
    }

}
