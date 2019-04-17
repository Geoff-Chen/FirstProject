package com.springTask.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class SeckillTask {

    @Scheduled(cron="* * * * * ?")
    public void refreshSeckillGoods(){
        System.out.println( (int) ((Math.random() * 9 + 1) * 100000));
        System.out.println("执行了任务调度"+new Date());
    }
}
