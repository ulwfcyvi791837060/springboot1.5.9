package com.yyx.aio.security;

import org.springframework.security.access.ConfigAttribute;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: ConfigAttribute
 * @author Yangkai 2017/11/30 14:46
 * @return
 */
public class UrlConfigAttribute implements ConfigAttribute {

    private final HttpServletRequest httpServletRequest;

    public UrlConfigAttribute(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }


    @Override
    public String getAttribute() {
        return null;
    }

    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }
}