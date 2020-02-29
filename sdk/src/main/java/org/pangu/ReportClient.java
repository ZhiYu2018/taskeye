package org.pangu;

import org.pangu.dto.PanguRequest;
import org.pangu.dto.PanguResponse;
import org.pangu.dto.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class ReportClient {
    private static volatile ReportClient INSTANCE = null;
    private static final int FLAG_START = 2;
    private static final int FLAG_END = 3;
    private static final int FLAG_FAILED = 4;
    private static Logger logger = LoggerFactory.getLogger(ReportClient.class);
    private static AtomicLong FLOW_ID = new AtomicLong(0);
    private TaskEyeRestCaller caller;
    private ConcurrentHashMap<String, String> taskOptId;
    @Autowired
    public ReportClient(Environment env){
        try{
            String url = env.getProperty("task.eye.url");
            logger.info("task.eye.url is:{}", url);
            caller = new TaskEyeRestCaller(url);
            taskOptId = new ConcurrentHashMap<>();
            INSTANCE = this;
        }catch (Throwable t){
            logger.warn("ReportClient init exceptions:", t);
        }
    }

    public static ReportClient getClient(){
        return INSTANCE;
    }

    public void sendJobStart(String appId, String jobId){
        String optId = createOptId();
        PanguRequest<TaskStatus> body = new PanguRequest<>();
        body.setAppId(appId);
        body.setFlowId(String.valueOf(FLOW_ID.incrementAndGet()));
        TaskStatus data = new TaskStatus();
        data.setAppId(appId);
        data.setTaskName(jobId);
        data.setOptId(optId);
        data.setMsg("Job begin");
        data.setFlag(FLAG_START);
        body.setData(data);

        try{
            PanguResponse<String> response = caller.PostStatus(body);
            if(response.getStatus().compareTo("200") == 0){
                taskOptId.put(String.format("%s.%s", appId,jobId), optId);
            }else{
                logger.info("sendJobStart failed:{}, msg:{}", response.getStatus(), response.getMsg());
            }
        }catch (Throwable t){
            logger.info("sendJobStart exceptions:{}",t.getMessage());
        }

    }

    public void sendJobEnd(String appId, String jobId, Integer err, String msg){
        String optId = taskOptId.get(String.format("%s.%s", appId,jobId));
        if(optId == null){
            return;
        }

        PanguRequest<TaskStatus> body = new PanguRequest<>();
        body.setAppId(appId);
        body.setFlowId(String.valueOf(FLOW_ID.incrementAndGet()));

        /**添加事件**/
        TaskStatus status = new TaskStatus();
        status.setAppId(appId);
        status.setTaskName(jobId);
        status.setOptId(optId);
        if((err == null) || err.intValue() == 0) {
            status.setMsg("Job Stopped");
            status.setFlag(FLAG_END);
        }else{
            status.setFlag(FLAG_FAILED);
            status.setMsg(msg);
            status.setErrorCode(err);
        }

        body.setData(status);
        try{
            PanguResponse<String> response = caller.PostStatus(body);
            if(response.getStatus().compareTo("200") != 0){
                logger.info("sendJobEnd failed:{}, msg:{}", response.getStatus(), response.getMsg());
            }
        }catch (Throwable t){
            logger.info("sendJobEnd exceptions:{}",t.getMessage());
        }
    }

    private static String createOptId(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }
}
