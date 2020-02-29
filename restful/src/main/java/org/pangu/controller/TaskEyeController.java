package org.pangu.controller;

import com.google.gson.Gson;
import org.pangu.api.TaskEyeServiceI;
import org.pangu.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController()
public class TaskEyeController {
    private static Logger logger = LoggerFactory.getLogger(TaskEyeController.class);

    @Autowired
    private TaskEyeServiceI taskEyeService;
    public TaskEyeController(){
        logger.info("TaskEyeController");
    }

    @PostMapping("/status")
    public Mono<PanguResponse<String>> PostStatus(ServerHttpRequest request, @RequestBody PanguRequest<TaskStatus> body){
        Gson gson = new Gson();
        logger.info("URL is:{}, flowId:{}, AppId:{}", request.getURI().toString(), body.getAppId(), body.getAppId());
        if(body.getData() == null){
            return Mono.just(new PanguResponse<String>(body.getFlowId(), "400", "Error args: data is null"));
        }

        TaskStatus data = body.getData();
        if(data.getAppId() == null || data.getTaskName() == null || data.getFlag() == null){
            return Mono.just(new PanguResponse<String>(body.getFlowId(), "400", "Error args"));
        }

        PanguResponse<String> response = taskEyeService.sendStatus(body);
        return Mono.just(response);
    }

    @PostMapping("/task")
    public Mono<PanguResponse<String>> PostTask(ServerHttpRequest request, @RequestBody PanguRequest<TaskInfo> body){
        logger.info("URL is:{}, flowId:{}, AppId:{}", request.getURI().toString(), body.getFlowId(), body.getAppId());
        if(body.getData() == null){
            return Mono.just(new PanguResponse<String>(body.getFlowId(), "400", "Error args: data is null"));
        }

        TaskInfo task = body.getData();
        if(task.getAppId() == null || task.getTaskName() == null){
            return Mono.just(new PanguResponse<String>(body.getFlowId(), "400", "Error args"));
        }

        PanguResponse<String> response = taskEyeService.setTask(body);
        return Mono.just(response);
    }

    private static <T> T convertTo(String json, Class<T> tClass){
        Gson gson = new Gson();
        return gson.fromJson(json, tClass);
    }
}
