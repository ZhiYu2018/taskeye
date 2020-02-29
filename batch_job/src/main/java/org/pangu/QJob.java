package org.pangu;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class QJob {
    private static Logger logger = LoggerFactory.getLogger(QJob.class);
    private Scheduler sched;

    public QJob(){
        SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
        try{
            sched = schedFact.getScheduler();
            sched.start();

            JobDetail job = newJob(HelloJob.class)
                    .withIdentity("40 second", "Teana") // name "myJob", group "group1"
                    .build();

            Trigger trigger = newTrigger()
                    .withIdentity("40 second", "Teana")
                    .startNow()
                    .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(40)
                            .repeatForever())
                    .build();
            sched.scheduleJob(job, trigger);

            job = newJob(HelloJob.class)
                    .withIdentity("Per 40", "Teana") // name "myJob", group "group1"
                    .build();
            trigger = newTrigger()
                    .withIdentity("Per 40", "Teana")
                    .startNow()
                    .withSchedule(cronSchedule("40 * * * * ?"))
                    .build();
            sched.scheduleJob(job, trigger);

            job = newJob(HelloJob.class)
                    .withIdentity("Per 40 two", "Teana") // name "myJob", group "group1"
                    .build();
            trigger = newTrigger()
                    .withIdentity("Per 40 two", "Teana")
                    .startNow()
                    .withSchedule(cronSchedule("/40 * * * * ?"))
                    .build();
            sched.scheduleJob(job, trigger);

        }catch (Throwable t){
            logger.warn("Init exceptions:", t);
        }

    }

    public void stop(){
        if(sched != null){
            try{
                sched.shutdown();
            }catch (Throwable t){
                logger.warn("stop exceptions:", t);
            }
        }
    }
}
