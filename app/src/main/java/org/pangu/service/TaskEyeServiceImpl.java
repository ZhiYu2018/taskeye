package org.pangu.service;

import org.pangu.api.TaskEyeServiceI;
import org.pangu.dto.PanguRequest;
import org.pangu.dto.PanguResponse;
import org.pangu.dto.TaskInfo;
import org.pangu.dto.TaskStatus;
import org.pangu.event.CmdBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskEyeServiceImpl implements TaskEyeServiceI {

    private static Logger logger = LoggerFactory.getLogger(TaskEyeServiceImpl.class);
    @Autowired
    private CmdBus cmdBus;
    public TaskEyeServiceImpl(){
        logger.info("TaskEyeServiceImpl create");
    }
    @Override
    public PanguResponse<String> sendStatus(PanguRequest<TaskStatus> taskStatus) {
        cmdBus.submitTaskStatus(taskStatus);
        return new PanguResponse<String>(taskStatus.getFlowId(), "200", "OK");
    }

    @Override
    public PanguResponse<String> setTask(PanguRequest<TaskInfo> taskInfo) {
        cmdBus.submitTaskConf(taskInfo);
        return new PanguResponse<String>(taskInfo.getFlowId(), "200", "OK");
    }
}
