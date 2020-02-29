package org.pangu.outbox.impl;

import feign.Feign;
import feign.Request;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import org.pangu.outbox.AlarmNotify;
import org.pangu.vo.AlarmEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class AlarmNotifyImpl implements AlarmNotify {
    private static final int MAX_CACHE_SIZE = 1000;
    private static Logger logger = LoggerFactory.getLogger(AlarmNotifyImpl.class);
    /**用于告警收敛**/
    private LRUCache<Backoff> alarmBackoff;
    private AlarmOutBox outBox;
    @Autowired
    public AlarmNotifyImpl(Environment env){
        alarmBackoff = new LRUCache<>(MAX_CACHE_SIZE);
        String context = env.getProperty("task.eye.alarm.enable");
        String url     = env.getProperty("task.eye.alarm.url");
        logger.info("task.eye.alarm.enable is: {}", context);
        if(context == null || (context.compareTo("true") !=0) || (url == null)){
            return ;
        }

        try{
            Request.Options options = new Request.Options(5L, TimeUnit.SECONDS, 5L,
                    TimeUnit.SECONDS, false);
            outBox = Feign.builder().options(options).encoder(new GsonEncoder()).
                     decoder(new GsonDecoder()).target(AlarmOutBox.class, url);
        }catch (Throwable t){
            logger.warn("Connect to:{},exceptions:{}", url, t.getMessage());
        }

    }

    @Override
    public void notify(AlarmEvent event) {
        String key = getKey(event);
        Backoff backoff = alarmBackoff.get(key);
        if(backoff == null){
            backoff = new Backoff();
            alarmBackoff.put(key, backoff);
        }

        if(!backoff.isTimeArrived()){
            return;
        }

        logger.info("Time:{}, AppId:{}, JobName:{}, OptId:{}, Action:{}, Msg:{}", event.getTime(), event.getAppId(),
                    event.getJobName(), event.getOptId(), event.getAction(), event.getMsg());

        if(outBox != null){
            try{
                outBox.submitEvent(event);
            }catch (Throwable t){
                logger.warn("Submit event Exceptions:{}", t.getMessage());
            }
        }
    }

    @Override
    public void batch(List<AlarmEvent> events) {
        Iterator<AlarmEvent> iterator = events.iterator();
        while(iterator.hasNext()){
            AlarmEvent event = iterator.next();
            String key = getKey(event);
            Backoff backoff = alarmBackoff.get(key);
            if(backoff == null){
                backoff = new Backoff();
                alarmBackoff.put(key, backoff);
            }

            if(!backoff.isTimeArrived()){
                iterator.remove();
                continue;
            }

            logger.info("Time:{}, AppId:{}, JobName{}, OptId:{}, Action:{}, Msg:{}",
                         event.getTime(), event.getAppId(),
                         event.getJobName(), event.getOptId(),
                         event.getAction(), event.getMsg());
        }
        if(outBox != null){
            try{
                outBox.batchEvent(events);
            }catch (Throwable t){
                logger.warn("Submit event Exceptions:{}", t.getMessage());
            }
        }
    }

    private static String getKey(AlarmEvent event){
       StringBuilder sb = new StringBuilder();
       sb.append(event.getAppId())
               .append(".")
               .append(event.getJobName())
               .append(".")
               .append(event.getAction());
       return sb.toString();
    }
}
