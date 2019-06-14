package com.yyx.aio.service.impl;

import com.yyx.aio.entity.Permission;
import com.yyx.aio.service.PermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户权限
 * @author Yangkai
 * @create 2017-11-28-11:47
 */
@Service("permissionService")
public class PermissionServiceImpl implements PermissionService{
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public List<Permission> getByUserId(Long id) {
        return null;
    }
}
