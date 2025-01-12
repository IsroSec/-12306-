package com.jiawa.train.batch.job;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.jiawa.train.batch.config.BatchApplication;
import com.jiawa.train.batch.feign.BusinessFeign;
import com.jiawa.train.common.resp.CommonResp;
import jakarta.annotation.Resource;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

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

    @Resource
    private BusinessFeign businessFeign;
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        MDC.put("LOG_ID", System.currentTimeMillis() + RandomUtil.randomString(3));
        LOG.info("生成每日车次信息开始");
        Date date = new Date();
        DateTime dateTime = DateUtil.offsetDay(date, 15);
        Date jdkDate = dateTime.toJdkDate();
        CommonResp<Object> objectCommonResp = businessFeign.genDaily(jdkDate);
        LOG.info("生成每日车次信息结束");
    }
}
