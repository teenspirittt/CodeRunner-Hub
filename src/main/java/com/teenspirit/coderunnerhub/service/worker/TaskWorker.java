package com.teenspirit.coderunnerhub.service.worker;
import com.teenspirit.coderunnerhub.dto.TestRequestDTO;
import com.teenspirit.coderunnerhub.service.ProblemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskWorker {

    private final ProblemService problemService;

    @Autowired
    public TaskWorker(ProblemService problemService) {
        this.problemService = problemService;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskWorker.class);

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void processTask(TestRequestDTO testRequestDTO) {
        LOGGER.info(String.format("Received message -> %s", testRequestDTO));
        problemService.processTestRequest(testRequestDTO);
    }
}