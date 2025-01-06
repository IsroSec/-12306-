package com.jiawa.train.batch.job;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * ClassName: TestJov
 * Package: com.jiawa.train.batch.job
 * Description:
 *
 * @Author GalSec
 * @Create 2025/1/6 15:24
 * @Version 1.0
 */
@DisallowConcurrentExecution
public class TestJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("testJob");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("结束");
    }
}
