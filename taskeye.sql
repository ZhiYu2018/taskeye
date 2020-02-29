CREATE TABLE `timer_lock`(
`app_id` varchar(32) NOT NULL DEFAULT '' COMMENT '接入方ID',
`lock_owner` varchar(64) NOT NULL DEFAULT '' COMMENT '锁的拥有者',
`lock_name` varchar(64) NOT NULL DEFAULT '' COMMENT '锁的名称',
`lock_status` int NOT NULL DEFAULT 0 COMMENT '1 释放, 2 锁',
`create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
`update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
 PRIMARY KEY `idx_lock` (`app_id`, `lock_name`),
 KEY `idx_ct` (`create_time`)
)ENGINE=InnoDB  DEFAULT CHARSET=UTF8MB4;

CREATE TABLE `job_info`(
`id` BIGINT NOT NULL AUTO_INCREMENT,
`app_id` varchar(32) NOT NULL COMMENT '接入方ID',
`status` tinyint(4)  NOT NULL DEFAULT 0 COMMENT '状态 1: 初始化、2：启用、3：禁用、4：结束',
`job_name` varchar(64) NOT NULL COMMENT '定时任务名称',
`time_expr` varchar(64) NOT NULL COMMENT '定时任务时间表达式',
`job_flag` tinyint(4) NOT NULL DEFAULT 1 COMMENT '1: IDEL, 2: 运行中',
`time_delay` int NOT NULL COMMENT '允许的超时，单位秒',
`time_used` BIGINT NOT NULL DEFAULT 0 COMMENT '预估的处理时间，用于挂住监控，0表示不监控',
`time_out_times` BIGINT NOT NULL DEFAULT 0 COMMENT '累计超时数次',
`trig_exec_times` BIGINT NOT NULL DEFAULT 0 COMMENT '累计触发数次',
`last_exec_times` BIGINT NOT NULL DEFAULT 0 COMMENT '检验累计触发数次',
`failed_times` BIGINT NOT NULL DEFAULT 0 COMMENT '累计执行失败数次', 
`next_time`   datetime NOT NULL COMMENT '下次执行时间',
`job_optId` varchar(64) NOT NULL DEFAULT "1900-01-01 00:00:00" COMMENT '定时执行流水号 yyyy-mm-ddHHMMSS',
`create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
`update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (`id`),
UNIQUE KEY `idx_job` (`app_id`, `job_name`), 
KEY `idx_nt` (`next_time`),
KEY `idx_ct` (`create_time`)
)ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE `schedule_last_time`(
`app_id` varchar(32) NOT NULL DEFAULT '' COMMENT '接入方ID',
`job_name` varchar(64) NOT NULL DEFAULT '' COMMENT '定时任务名称',
`last_time` datetime NOT NULL  COMMENT '上次执行任务的时间',
`create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
`update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY `idx_pk` (`app_id`, `job_name`),
KEY `idx_ct` (`create_time`)
)ENGINE=InnoDB  DEFAULT CHARSET=UTF8;

CREATE TABLE `job_triggers_log`(
`id` BIGINT NOT NULL AUTO_INCREMENT,
`status` tinyint(4)  NOT NULL DEFAULT 1 COMMENT '执行状态 2: start、3：结束、4：失败、5：超时',
`alarm` tinyint(4)  NOT NULL DEFAULT 1 COMMENT '状态 1: 未告警,2:已经告警,3:不必告警',
`app_id` varchar(32) NOT NULL COMMENT '接入方ID',
`job_name` varchar(64) NOT NULL COMMENT '定时任务名称',
`job_optId` varchar(64) NULL DEFAULT "1900-01-01 00:00:00" COMMENT '定时执行流水号 yyyy-mm-ddHHMMSS',
`job_msg`  varchar(256) NOT NULL DEFAULT 'Running' COMMENT '执行结果原因',
`job_time`   datetime NOT NULL COMMENT '计划执行时间',
`over_time` datetime NOT NULL COMMENT '预计执行结束时间',
`create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
`update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (`id`),
UNIQUE KEY `udx_opt` (`app_id`, `job_name`, `job_optId`), 
KEY `idx_ot` (`over_time`),
KEY `idx_ct` (`create_time`)
)ENGINE=InnoDB  DEFAULT CHARSET=UTF8;