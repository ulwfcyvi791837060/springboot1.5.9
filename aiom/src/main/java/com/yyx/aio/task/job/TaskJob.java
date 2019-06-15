package com.yyx.aio.task.job;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.yyx.aio.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 定时任务
 * </p>
 *
 * @package: com.yyx.aio.task.job
 * @description: 定时任务
 * @author: yangkai.shen
 * @date: Created in 2018/11/22 19:09
 * @copyright: Copyright (c) 2018
 * @version: V1.0
 * @modified: yangkai.shen
 */
@Component
//@Slf4j
public class TaskJob {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    UserService UserServiceImpl;

/*1.Summary 日销售汇总表
2.Business 营业明细表
3.Bill Detail  账单销售明细表
4.Paytype Detail 支付方式明细表
5.Discount Detail 优惠金额明细表

表1是一天传一次完整的，表2-5是按流水传，3-5分钟传一次即可*/


    /**
     * 按照标准时间来算，每隔 10s 执行一次
     */
    /*@Scheduled(cron = "0/10 * * * * ?")
    public void job1() {
        log.info("【job1】开始执行：{}", DateUtil.formatDateTime(new Date()));
    }*/

    /**
     * 从启动时间开始，间隔 2s 执行
     * 固定间隔时间
     */
    @Scheduled(fixedRate = 5*60*1000)
    public void job2() {
        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMdd");
        // 创建Date对象，表示当前时间
        Date now = new Date();
        log.info("【job22】开始执行：{}", DateUtil.formatDateTime(now));

        UserServiceImpl.uploadAction(sdf3.format(now),true);
    }

    /**
     * 从启动时间开始，延迟 5s 后间隔 4s 执行
     * 固定等待时间
     */
    /*@Scheduled(fixedDelay = 4000, initialDelay = 5000)
    public void job3() {
        log.info("【job3】开始执行：{}", DateUtil.formatDateTime(new Date()));
    }*/
}