package org.pangu;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class HelloJob implements Job {
    private static Logger logger = LoggerFactory.getLogger(HelloJob.class);
    private Random random;
    public HelloJob(){
        random = new Random();
    }

    @Override
    public void execute(JobExecutionContext jobContext) throws JobExecutionException {
        JobKey key = jobContext.getJobDetail().getKey();
        ReportClient.getClient().sendJobStart(key.getGroup(), key.getName());
        int r = random.nextInt(10000);
        try {
            logger.info("AppId:{}, Job:{}", key.getGroup(), key.getName());
            Thread.sleep(r);
            if((r % 4) <= 2){
                ReportClient.getClient().sendJobEnd(key.getGroup(), key.getName(), Integer.valueOf(0), "Stopped");
            }else if((r % 4) > 2){
                ReportClient.getClient().sendJobEnd(key.getGroup(), key.getName(), Integer.valueOf(-1), "Exceptions");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
