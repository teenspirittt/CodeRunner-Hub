package com.teenspirit.coderunnerhub.containermanager;


import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Volume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ContainerManager {

    private final Map<String, Container> containers;
    private final DockerClient dockerClient;


    @Autowired
    public ContainerManager(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
        this.containers = new HashMap<>();
    }

    public Container createContainer(String imageName, String sourceCode, String workingDirectory) {
        String containerId = createContainerInternal(imageName, sourceCode, workingDirectory);
        Container container = new Container(containerId, imageName, sourceCode, workingDirectory);
        containers.put(containerId, container);
        return container;
    }

    public void startContainer(Container container) {
        dockerClient.startContainerCmd(container.getId()).exec();
        container.incrementActiveUsages();
    }

    public void stopContainer(Container container) {
        dockerClient.stopContainerCmd(container.getId()).exec();
        containers.remove(container.getId());
    }

    public void removeContainer(Container container) {
        dockerClient.removeContainerCmd(container.getId()).exec();
        containers.remove(container.getId());
    }

    private String createContainerInternal(String imageName, String sourceCode, String workingDirectory){
        CreateContainerResponse container = dockerClient.createContainerCmd(imageName)
                .withHostConfig(
                        HostConfig.newHostConfig()
                                .withBinds(new Bind(sourceCode, new Volume(workingDirectory))))
                .exec();

        return container.getId();
    }
}
