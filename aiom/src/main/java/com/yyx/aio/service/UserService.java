package com.yyx.aio.service;

import com.yyx.aio.entity.Result;
import com.yyx.aio.entity.User;

public interface UserService {
    User getByLoginName(String userName);

    Result uploadAction(String date, boolean b);
}
