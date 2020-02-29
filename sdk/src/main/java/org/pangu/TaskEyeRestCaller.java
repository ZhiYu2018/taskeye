package org.pangu;

import feign.Feign;
import feign.Request;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import org.pangu.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;

public class TaskEyeRestCaller {
    private static final PanguResponse<String> DEF_ERROR = new PanguResponse<>("0000", "410","caller is null");
    private static Logger logger = LoggerFactory.getLogger(TaskEyeRestCaller.class);
    private TaskEyeRest rest;

    public TaskEyeRestCaller(String url){
            Request.Options options = new Request.Options(1L, TimeUnit.SECONDS, 1L,
                    TimeUnit.SECONDS, false);
            rest = Feign.builder().options(options).encoder(new GsonEncoder())
                    .decoder(new GsonDecoder()).target(TaskEyeRest.class, url);
    }

    public PanguResponse<String> PostStatus(PanguRequest<TaskStatus> body) {
        if(rest == null){
            return DEF_ERROR;
        }

        return rest.PostStatus(body);
    }


    public PanguResponse<String> PostTask(PanguRequest<TaskInfo> body) {
        if(rest == null){
            return DEF_ERROR;
        }
        return rest.PostTask(body);
    }
}
