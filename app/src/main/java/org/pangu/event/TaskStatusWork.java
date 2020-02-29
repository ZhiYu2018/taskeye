package org.pangu.event;

import org.pangu.dto.PanguRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class TaskStatusWork<T> implements Runnable{
    private static Logger logger = LoggerFactory.getLogger(TaskStatusWork.class);
    private PanguRequest<T> taskStatus;
    private Consumer<PanguRequest<T>> consumer;
    public TaskStatusWork(PanguRequest<T> request, Consumer<PanguRequest<T>> consumer){
        taskStatus = request;
        this.consumer = consumer;
    }
    @Override
    public void run() {
        try{
            consumer.accept(taskStatus);
        }catch (Throwable t){
            logger.error("accept exception:", t);
        }
    }
}
