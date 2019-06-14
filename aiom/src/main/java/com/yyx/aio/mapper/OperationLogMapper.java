package com.yyx.aio.mapper;


import com.yyx.aio.entity.OperationLog;

public interface OperationLogMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OperationLog record);

    int insertSelective(OperationLog record);

    OperationLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OperationLog record);

    int updateByPrimaryKey(OperationLog record);
}