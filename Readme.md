# 说明

## 用途

**该程序是用于对定时任务进行监控**。

* 基于cron 表达式的定时任务。
* 基于每隔多长时间的定时任务。

## 部署

* 先利用SQL创建表。
* task.monitor：参考配置文件.
* restful: 参考配置文件

## 使用

* 参考 batch_job

## 告警适配

提供如下协议的http接口

```java
public interface AlarmOutBox {
    @RequestLine("POST /alarm/submit")
    @Headers("Content-Type: application/json")
    String submitEvent(AlarmEvent event);
    @RequestLine("POST /alarm/submit")
    @Headers("Content-Type: application/json")
    String batchEvent(List<AlarmEvent> events);
}
```

并在monitor 程序中启用如下的配置：

```yaml
task: 
 eye: 
  alarm: 
   enable: false
   url:    
```

