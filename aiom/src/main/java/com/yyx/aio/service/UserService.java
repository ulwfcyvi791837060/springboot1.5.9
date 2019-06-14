package com.yyx.aio.service;

import com.yyx.aio.entity.User;

public interface UserService {
    User getByLoginName(String userName);
}
