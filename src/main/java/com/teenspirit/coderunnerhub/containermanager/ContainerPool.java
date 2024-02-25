package com.teenspirit.coderunnerhub.containermanager;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.HostConfig;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
        return pool.values().stream()
                .filter(Container::isAvailable)
                .findFirst()
                .orElseGet(() -> {
                    Container container = createContainer();
                    startContainer(container);
                    return container;
                });
    }

    public void releaseContainer(String containerId) {
        pool.compute(containerId, (id, container) -> {
            if (container != null) {
                container.decrementActiveUsages();
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
        container.incrementActiveUsages();
    }

    public void removeContainer(Container container) {
        dockerClient.removeContainerCmd(container.getId()).exec();
        pool.remove(container.getId());
    }

}

