package com.yyx.aio.task.job;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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
@Slf4j
public class TaskJob {
    //private Logger log = LoggerFactory.getLogger(getClass());

/*1.Summary 日销售汇总表
2.Business 营业明细表
3.Bill Detail  账单销售明细表
4.Paytype Detail 支付方式明细表
5.Discount Detail 优惠金额明细表

表1是一天传一次完整的，表2-5是按流水传，3-5分钟传一次即可*/


    /**
     * 按照标准时间来算，每隔 10s 执行一次
     */
    //@Scheduled(cron = "0/10 * * * * ?")
    public void job1() {
        log.info("【job1】开始执行：{}", DateUtil.formatDateTime(new Date()));
    }

    /**
     * 从启动时间开始，间隔 2s 执行
     * 固定间隔时间
     */
    @Scheduled(fixedRate = 2000)
    public void job2() {

        log.info("【job2】开始执行：{}", DateUtil.formatDateTime(new Date()));

        //String url = "/api/data/str";
        //测试环境：https://lb-test.tcsl.com.cn:8079/bi_proxy/
        //String url = "https://lb-test.tcsl.com.cn:8079/bi_proxy//api/data/str";
        String url = "https://bi.tcsl.com.cn:8055/lb/api/data/str";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        

        String param = "{\n" +
                "    \"columnNames\": [\n" +
                "        \"location_id\",\n" +
                "        \"store_id\",\n" +
                "        \"store_name\",\n" +
                "        \"b_date\",\n" +
                "        \"s_receivable\",\n" +
                "        \"s_real_income\",\n" +
                "        \"s_bill_num\",\n" +
                "        \"s_discount_total\",\n" +
                "        \"s_chargeback\",\n" +
                "        \"s_chargeback_num\",\n" +
                "        \"s_time\",\n" +
                "        \"s_refresh_time\"\n" +
                "    ],\n" +
                "    \"keyCol\": \"store_id,b_date\",\n" +
                "    \"records\": [\n" +
                "        [\n" +
                "            \"7023\",\n" +
                "            \"01060125\",\n" +
                "            \"满记甜品\",\n" +
                "            \"2018-08-02 00:00:00\",\n" +
                "            \"1.0\",\n" +
                "            \"0.8\",\n" +
                "            \"2.0\",\n" +
                "            \"0.2\",\n" +
                "            \"2.0\",\n" +
                "            \"0.0\",\n" +
                "            \"0.0\",\n" +
                "            \"2019-06-09 16:37:46\",\n" +
                "            \"2019-06-09 16:37:46\",\n" +
                "        ]\n" +
                "    ],\n" +
                "    \"tableName\": \"Summary\"\n" +
                "}";

        String publicKeyStr = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCIsGBo7H2RwlwS0p01THCCA8vX6keZ143G+pP1MMtDve9lPPgRt2IAUAmGc/79a9O69C1u5j+ebdK9a5BfjXwQcyEgV2nRlJjr83O0zwoTp6Mc4WuT5ACNGrHUdijxBW9O+pZRmql5nZES8HrkKb0EtsF6PRguqmFsxg1t3eeqSQIDAQAB";

        Map<String, String> map = new HashMap<String, String>();
        //企业秘钥DES加密转base64
        map.put("data",com.yyx.aio.task.util.Base64Utils.encode(com.yyx.aio.task.util.DESUtil.encrypt(param,desKey)));
        try {
            //RSA公钥加密转base64
            map.put("corporationCode",com.yyx.aio.task.util.RSAUtil.encrypt(
                    com.yyx.aio.task.util.RSAUtil.loadPublicKey(publicKeyStr),"000062".getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpEntity<String> httpEntity = new HttpEntity<>(JSON.toJSONString(map), headers);
        String result = restTemplate.postForObject(url, httpEntity, String.class);
        log.info("结果=>" + result);
    }

    /**
     * 从启动时间开始，延迟 5s 后间隔 4s 执行
     * 固定等待时间
     */
    //@Scheduled(fixedDelay = 4000, initialDelay = 5000)
    public void job3() {
        log.info("【job3】开始执行：{}", DateUtil.formatDateTime(new Date()));
    }
}