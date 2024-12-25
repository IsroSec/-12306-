package com.jiawa.train.common.util;

import cn.hutool.core.util.IdUtil;

/**
 * 封装hutool雪花算法
 */
public class SnowUtil {

    /**
     * 分布式系统如何保证数据中心和机器标识的全局唯一性
     * 可以将机器标识和数据中心作为redis的key，通过redis的自增实现
     * 这样就保证了全局唯一
     */
    private static long dataCenterId = 1;  //数据中心
    private static long workerId = 1;     //机器标识

    public static long getSnowflakeNextId() {
        return IdUtil.getSnowflake(workerId, dataCenterId).nextId();
    }

    public static String getSnowflakeNextIdStr() {
        return IdUtil.getSnowflake(workerId, dataCenterId).nextIdStr();
    }
}
