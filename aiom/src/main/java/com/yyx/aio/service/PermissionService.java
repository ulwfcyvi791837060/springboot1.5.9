package com.yyx.aio.service;

import com.yyx.aio.entity.Permission;

import java.util.List;

public interface PermissionService {
    List<Permission> getByUserId(Long id);
}
