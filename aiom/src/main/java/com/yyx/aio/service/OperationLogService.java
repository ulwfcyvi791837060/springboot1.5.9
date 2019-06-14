package com.yyx.aio.service;

import com.yyx.aio.entity.OperationLog;

/*
 * @Description:
 * @author Yangkai 2017/11/28 10:40
 * @return
 */
public interface OperationLogService {
    void insertSelective(OperationLog operationLog);
}
