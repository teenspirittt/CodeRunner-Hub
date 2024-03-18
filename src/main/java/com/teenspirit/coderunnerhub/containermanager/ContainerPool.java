package com.teenspirit.coderunnerhub.containermanager;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.HostConfig;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class ContainerPool {

    @Getter
    private final ConcurrentMap<String, Container> pool;
    private final DockerClient dockerClient;

    @Value("${docker.image.name}")
    private String defaultImageName;

    public ContainerPool(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
        this.pool = new ConcurrentHashMap<>();
    }

    public Container getContainer() {
        // Find the first available container in the pool
        Optional<Container> availableContainer = pool.values().stream()
                .filter(Container::isAvailable)
                .findFirst();

        // If an available container is found, mark it as unavailable and return
        Container container;
        if (availableContainer.isPresent()) {
            container = availableContainer.get();
            container.setAvailable(false); // Mark the container as unavailable
        }
        // If no available containers are found, create a new one and start it
        else {
            container = createContainer();
            startContainer(container);
        }
        return container;
    }

    public void releaseContainer(String containerId) {
        pool.compute(containerId, (id, container) -> {
            if (container != null) {
                container.setAvailable(true);
            }
            return container;
        });
    }

    public Container createContainer() {
        String containerId = createContainerInternal(defaultImageName);
        Container container = new Container(containerId, defaultImageName);
        pool.put(containerId, container);
        return container;
    }

    private String createContainerInternal(String imageName) {
        try {
            CreateContainerResponse container = dockerClient.createContainerCmd(imageName)
                    .withHostConfig(HostConfig.newHostConfig()
                    ).exec();

            return container.getId();
        } catch (Exception e) {
            throw new RuntimeException("Error creating container", e);
        }
    }

    public void startContainer(Container container) {
        dockerClient.startContainerCmd(container.getId()).exec();
        container.setAvailable(true);
    }

    public void removeContainer(Container container) {
        dockerClient.removeContainerCmd(container.getId()).exec();
        pool.remove(container.getId());
    }
}

