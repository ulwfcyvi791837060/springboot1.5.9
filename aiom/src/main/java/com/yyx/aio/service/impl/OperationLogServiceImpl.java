package com.yyx.aio.service.impl;

import com.yyx.aio.entity.OperationLog;
import com.yyx.aio.mapper.OperationLogMapper;
import com.yyx.aio.service.OperationLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * controller访问日志
 * @author Xhero
 * @create 2017-11-28-10:41
 */
@Service("operationLogService")
public class OperationLogServiceImpl implements OperationLogService{
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired(required = false)
    private OperationLogMapper operationLogMapper;

    @Override
    public void insertSelective(OperationLog operationLog) {
        try {
            operationLogMapper.insertSelective(operationLog);
        }catch (Exception e){
            logger.error("operationLogMapper.insertSelective");
        }
    }
}
