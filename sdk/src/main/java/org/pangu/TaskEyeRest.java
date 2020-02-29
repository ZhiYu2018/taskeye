package org.pangu;


import feign.Headers;
import feign.RequestLine;
import org.pangu.dto.*;

public interface TaskEyeRest {
    @RequestLine("POST /rest/status")
    @Headers("Content-Type: application/json")
    PanguResponse<String> PostStatus(PanguRequest<TaskStatus> body);
    @RequestLine("POST /rest/task")
    @Headers("Content-Type: application/json")
    PanguResponse<String> PostTask(PanguRequest<TaskInfo> body);
}
