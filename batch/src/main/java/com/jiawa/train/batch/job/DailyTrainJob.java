package com.jiawa.train.batch.job;

import cn.hutool.core.util.RandomUtil;
import com.jiawa.train.batch.config.BatchApplication;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

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
public class DailyTrainJob implements Job {
    private static final Logger LOG = LoggerFactory.getLogger(BatchApplication.class);
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        MDC.put("LOG_ID", System.currentTimeMillis() + RandomUtil.randomString(3));
        LOG.info("生成每日车次信息开始");
        LOG.info("生成每日车次信息结束");
    }
}
