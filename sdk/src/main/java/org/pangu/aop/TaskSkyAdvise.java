package org.pangu.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.pangu.ReportClient;
import org.pangu.SkyEyeTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class TaskSkyAdvise {
    private static Logger logger = LoggerFactory.getLogger(TaskSkyAdvise.class);
    private final ReportClient client;
    @Autowired
    public TaskSkyAdvise(ReportClient client){
        if(client == null){
            logger.warn("ReportClient is null");
        }
        this.client = client;
    }

    @Around("@annotation(org.pangu.SkyEyeTarget)")
    public Object watchLifeCycle(ProceedingJoinPoint joinPoint) throws Throwable {
        SkyEyeTarget target = getSkyEyeTarget(joinPoint);
        begin(target);
        try{
            Object obj = joinPoint.proceed();
            end(target, 0, "Stopped");
            return obj;
        }catch (Throwable t) {
            end(target, -100, t.getMessage());
            throw t;
        }
    }

    private SkyEyeTarget getSkyEyeTarget(ProceedingJoinPoint joinPoint){
        try{
            String className = joinPoint.getTarget().getClass().getSimpleName();
            String methodName = joinPoint.getSignature().getName();
            Object[] args = joinPoint.getArgs();

            Class<?> classTarget = joinPoint.getTarget().getClass();
            Class<?>[] par = ((MethodSignature) joinPoint.getSignature()).getParameterTypes();
            Method objMethod = classTarget.getMethod(methodName, par);

            SkyEyeTarget target = objMethod.getAnnotation(SkyEyeTarget.class);
            return target;
        }catch (Throwable t){
            logger.info("getSkyEyeTarget failed:{}", t.getMessage());
            return null;
        }
    }

    private void begin(SkyEyeTarget target){
        try{
            client.sendJobStart(target.app(), target.job());
        }catch (Throwable t){
            logger.info("begin exceptions:{}", t.getMessage());
        }
    }

    private void end(SkyEyeTarget target, int err, String msg){
        try{
            client.sendJobEnd(target.app(), target.job(), Integer.valueOf(err), msg);
        }catch (Throwable t){
            logger.info("end exceptions:{}", t.getMessage());
        }
    }
}
