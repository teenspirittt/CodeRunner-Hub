package com.teenspirit.coderunnerhub.service.worker;
import com.teenspirit.coderunnerhub.service.ProblemService;
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

    @RabbitListener(queues = "task_queue")
    public void processTask(String taskId) {
        // Здесь выполняется код обработки задания

    }
}