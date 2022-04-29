package com.micropower.basic.timer;

import com.micropower.basic.service.OperationRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Date: 2021/2/20 10:25
 * @Description: TODO → 定时清理任务
 * @Author:Kohaku_川
 **/
@Component
public class CleanupTask {

    private static CleanupTask task;
    @Autowired
    private OperationRecordService operationRecordService;

    @PostConstruct
    public void init() {
        task = this;
        task.operationRecordService = this.operationRecordService;
    }

    /**
     * @Author kohaku_C
     * @Description //TODO 每天0点定时清理超过5天的原始报文记录和转发记录
     * @Date  11:22  2021/3/16
     * @Param
     * @return
     **/
    @Scheduled(cron = " 0 0 0 * * ?")
    private void cleanUpMessage(){
        task.operationRecordService.cleanUpMessage();
    }
}
