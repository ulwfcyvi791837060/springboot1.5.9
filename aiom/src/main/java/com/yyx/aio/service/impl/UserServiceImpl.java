package com.yyx.aio.service.impl;

import com.yyx.aio.entity.User;
import com.yyx.aio.mapper.UserMapper;
import com.yyx.aio.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Xhero
 * @create 2017-11-28-11:43
 */
@Service("userService")
public class UserServiceImpl implements UserService {
    @Autowired(required = false)
    private UserMapper userMapper;

    private Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    public User getByLoginName(String loginName) {
        try {
            Map map = new HashMap();
            map.put("loginName",loginName);
            logger.info("根据用户名获取用户！");
            return userMapper.getByUserName(map);
        } catch (Exception e){
            logger.error(getClass().getName() + "error");
            return null;
        }
    }
}
