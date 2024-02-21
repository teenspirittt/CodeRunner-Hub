package com.teenspirit.coderunnerhub.containermanager;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.HostConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class ContainerPool {

    private final ConcurrentMap<String, Container> pool;
    private final DockerClient dockerClient;

    @Value("${docker.image.name}")
    private String defaultImageName;

    public ContainerPool(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
        this.pool = new ConcurrentHashMap<>();
    }

    public Container getContainer(String containerId) {
        return pool.compute(containerId, (s, container) -> {
            if (container == null) {
                container = createContainer();
                startContainer(container);
            }
            container.incrementActiveUsages();
            return container;
        });
    }

    public void releaseContainer(String containerId) {
        pool.compute(containerId, (id, container) -> {
            if (container != null) {
                container.decrementActiveUsages();
                if (container.getActiveUsages() <= 0) {
                    removeContainer(container);
                    return null;
                }
            }

            return container;
        });
    }

    private Container createContainer() {
        String containerId = createContainerInternal(defaultImageName);
        Container container = new Container(containerId, defaultImageName);
        pool.put(containerId, container);
        return container;
    }

    private String createContainerInternal(String imageName) {
        try {
            CreateContainerResponse container = dockerClient.createContainerCmd(imageName)
                    .withHostConfig(
                            HostConfig.newHostConfig()
                    ).exec();

            return container.getId();
        } catch (Exception e) {
            throw new RuntimeException("Error creating container", e);
        }
    }

    private void startContainer(Container container) {
        dockerClient.startContainerCmd(container.getId()).exec();
        container.incrementActiveUsages();
    }

    private void removeContainer(Container container) {
        dockerClient.removeContainerCmd(container.getId()).exec();
        pool.remove(container.getId());
    }
}

