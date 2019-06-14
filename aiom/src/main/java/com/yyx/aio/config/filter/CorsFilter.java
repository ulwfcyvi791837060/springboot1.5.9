package com.yyx.aio.config.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description: 支持跨域访问
 * @author Yangkai 2017/11/30 14:45
 * @return
 */
public class CorsFilter implements Filter {
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletResponse res = (HttpServletResponse) response;
		HttpServletRequest  req = (HttpServletRequest) request;

        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS");
	    res.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Accept-Encoding, Accept-Language, Host, Referer, Connection, User-Agent, authorization, sw-useragent, sw-version");

		// Just REPLY OK if request method is OPTIONS for CORS (pre-flight)
		if ( req.getMethod().equals("OPTIONS") ) {
        res.setStatus(HttpServletResponse.SC_OK);
        return;
    }
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {}
}
