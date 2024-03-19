package com.teenspirit.coderunnerhub.service.worker;
import com.teenspirit.coderunnerhub.dto.TestRequestDTO;
import com.teenspirit.coderunnerhub.service.SolutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskWorker {
    private final SolutionService solutionService;

    @Autowired
    public TaskWorker(SolutionService solutionService) {
        this.solutionService = solutionService;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskWorker.class);

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void processTask(TestRequestDTO testRequestDTO) {
        LOGGER.info(String.format("Received message -> %s", testRequestDTO));
        solutionService.processTestRequest(testRequestDTO);
    }
}