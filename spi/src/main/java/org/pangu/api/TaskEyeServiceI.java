package org.pangu.api;

import org.pangu.dto.PanguRequest;
import org.pangu.dto.PanguResponse;
import org.pangu.dto.TaskInfo;
import org.pangu.dto.TaskStatus;

public interface TaskEyeServiceI {
    PanguResponse<String> sendStatus(PanguRequest<TaskStatus> taskStatus);
    PanguResponse<String> setTask(PanguRequest<TaskInfo> taskInfo);
}
