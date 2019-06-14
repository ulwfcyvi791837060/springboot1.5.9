package com.yyx.aio.config.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

/**
 * 监听登录失败。
 * @author Yangkai
 * @create 2017-11-29-12:46
 */
@Component
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent authenticationFailureBadCredentialsEvent) {
        String account = authenticationFailureBadCredentialsEvent.getAuthentication().getPrincipal().toString();
        /*Map<String, Object> user = userDao.queryUserByAccount(account);
        if (user != null) {
            // 用户失败次数
            int fails = Integer.parseInt(user.get("FAILS").toString());
            fails++;
            // 系统配置失败次数
            int FAILS_COUNT = Integer.parseInt(paramsDao.queryParamsValue("FAILS_COUNT"));
            // 超出失败次数，停用账户
            if (fails >= FAILS_COUNT) {
                userDao.updateStatusByAccount(account, "false", fails);
                // 失败次数++
            } else {
                userDao.updateStatusByAccount(account, user.get("ENABLE").toString(), fails);
            }
        }*/
    }
}
