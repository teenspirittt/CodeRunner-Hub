package com.teenspirit.coderunnerhub.util;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CopyArchiveToContainerCmd;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.async.ResultCallbackTemplate;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.teenspirit.coderunnerhub.containermanager.Container;
import com.teenspirit.coderunnerhub.containermanager.ContainerPool;
import com.teenspirit.coderunnerhub.model.ExecutionResult;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

@Component
public class CCodeExecutor {
    private final DockerClient dockerClient;
    private final ContainerPool containerPool;

    public CCodeExecutor(ContainerPool containerPool, DockerClient dockerClient) {
        this.dockerClient = dockerClient;
        this.containerPool = containerPool;
    }

    public ExecutionResult executeCode(File codeFile, String[] inputValues) {
        Container container = containerPool.getContainer();
        ExecutionResult result = executeCodeInContainer(container, codeFile, inputValues);
        containerPool.releaseContainer(container.getId());
        return result;
    }

    private ExecutionResult executeCodeInContainer(Container container, File codeFile, String[] inputValues) {
        String compileResult = compileCodeInContainer(container, codeFile);
        String containerPath = "/usr/src/app";

        // copy to container
        CopyArchiveToContainerCmd copyCmd = dockerClient.copyArchiveToContainerCmd(container.getId())
                .withHostResource(codeFile.getAbsolutePath())
                .withRemotePath(containerPath);
        copyCmd.exec();

        System.out.println("!!!start " + compileResult + " end!!!");

        if (!compileResult.isEmpty()) {
            return new ExecutionResult(null, compileResult); // Compilation error
        }

        return executeCompiledCodeInContainer(container, inputValues);
    }

    private String compileCodeInContainer(Container container, File codeFile) {
        String containerPath = "/usr/src/app";
        String fileName = codeFile.getName();
        String nameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));

        String compileCommand = "sh -c 'cd " + containerPath
                + " && gcc " + codeFile.getName()
                + " -o " + nameWithoutExtension + "'";

        return executeCommandInContainer(container, compileCommand);
    }

    private ExecutionResult executeCompiledCodeInContainer(Container container, String[] inputValues) {
        String containerPath = "/usr/src/app";
        String fileName = new File(containerPath).getName(); // Assuming the compiled binary has the same name as the source file

        String executeCommand = "sh -c 'cd " + containerPath
                + " && ./" + fileName
                + " " + arrayToString(inputValues, " ") + "'";

        return new ExecutionResult(executeCommandInContainer(container, executeCommand), null);
    }

    private String executeCommandInContainer(Container container, String command) {
        try {
            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(container.getId())
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .withCmd("sh", "-c", command)
                    .exec();

            // get stdout
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            dockerClient.execStartCmd(execCreateCmdResponse.getId())
                    .exec(new ResultCallbackTemplate<ExecStartResultCallback, Frame>() {
                        @Override
                        public void onNext(Frame item) {
                            try {
                                outputStream.write(item.getPayload());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .awaitCompletion();

            return outputStream.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error executing code in container", e);
        }
    }

    private String arrayToString(String[] array, String delimiter) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < array.length; i++) {
            result.append(array[i]);
            if (i < array.length - 1) {
                result.append(delimiter);
            }
        }

        return result.toString();
    }
}
