package com.yyx.aio.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
/**
 * @author wuwei
 * @time 2017 11 30
 */
@Data
public class OperationLog implements Serializable{
    private Long id;

    private Long userId;

    private Date operTime;

    private String requestUri;

    private Long requestMillis;

    private String requestParam;

    private String requestResult;
}