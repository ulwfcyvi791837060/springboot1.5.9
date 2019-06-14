package com.yyx.aio.security;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @Description:
 * @author Yangkai 2017/11/30 14:48
 * @return
 */
@Service
public class UrlMetadataSourceService implements
        FilterInvocationSecurityMetadataSource {

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        final HttpServletRequest request = ((FilterInvocation) object).getRequest();
        Set<ConfigAttribute> allAttributes = new HashSet<>();
        ConfigAttribute configAttribute = new UrlConfigAttribute(request);
        allAttributes.add(configAttribute);
        return allAttributes;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
