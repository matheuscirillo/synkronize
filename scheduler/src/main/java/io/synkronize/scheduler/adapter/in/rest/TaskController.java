package io.synkronize.scheduler.adapter.in.rest;

import io.synkronize.scheduler.core.SynkronizeTaskQueue;
import io.synkronize.scheduler.core.message.TaskMessage;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/tasks")
public class TaskController {

    private final Logger logger = LoggerFactory.getLogger(TaskController.class);

    private final SynkronizeTaskQueue taskQueue;

    public TaskController(SynkronizeTaskQueue taskQueue) {
        this.taskQueue = taskQueue;
    }

    @PUT
    @Path("/{taskId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void putTask(TaskMessage taskMessage, @PathParam("taskId") String taskId) throws InterruptedException {
        logger.info("Received task {} for {}", taskMessage, taskMessage.messageType());
        taskQueue.put(taskMessage);
        logger.info("TaskMessage of task {} enqueued", taskId);
    }
}
