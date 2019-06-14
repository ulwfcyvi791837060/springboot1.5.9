package com.yyx.aio.config.filter;

import com.alibaba.fastjson.JSONException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.yyx.aio.config.identity.TokenUtil;
import com.yyx.aio.config.session.SessionItem;
import com.yyx.aio.entity.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;


/* This filter maps to /login and tries to validate the username and password */
/**
 * @Description: 实现登录拦截。登录。
 * @author Yangkai 2017/11/30 14:45
 * @return
 */
public class GenerateTokenForUserFilter extends AbstractAuthenticationProcessingFilter {

    @Resource
    SessionRegistry sessionRegistry;

    private TokenUtil tokenUtil;

    public GenerateTokenForUserFilter(String urlMapping, AuthenticationManager authenticationManager, TokenUtil tokenUtil,SessionRegistry sessionRegistry) {
        super(new AntPathRequestMatcher(urlMapping));
        setAuthenticationManager(authenticationManager);
        this.tokenUtil = tokenUtil;
        this.sessionRegistry = sessionRegistry;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, JSONException, IOException {
        try {
            /* using org.json */
//            String jsonString = getStrFromInputSteam(request.getInputStream());
            /* using org.json */
//            JSONObject userJSON = JSON.parseObject(jsonString);

//            String username = userJSON.getString("username");
//            String password = userJSON.getString("password");
            String username = request.getParameter("username");
            String password = request.getParameter("password");

            //final UsernamePasswordAuthenticationToken loginToken = new UsernamePasswordAuthenticationToken("demo", "demo");
            final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
            return getAuthenticationManager().authenticate(authToken); // This will take to successfulAuthentication or faliureAuthentication function
        } catch (JSONException | AuthenticationException e) {
            throw new AuthenticationServiceException(e.getMessage());
        }
    }

    @Override
    public void setSessionAuthenticationStrategy(SessionAuthenticationStrategy sessionStrategy){
        new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication authToken) throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(authToken);
        sessionRegistry.registerNewSession(req.getSession().getId(),authToken.getPrincipal());
        User tokenUser = (User) authToken.getPrincipal();
        SessionItem respItem = new SessionItem();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String tokenString = this.tokenUtil.createTokenForUser(tokenUser);

        respItem.setLoginName(tokenUser.getLoginName());
        respItem.setUserName(tokenUser.getUserName());
        respItem.setUserId(tokenUser.getId());
        respItem.setEmail(tokenUser.getMobile());
        respItem.setToken(tokenString);


        String jsonRespString = ow.writeValueAsString(respItem);

        res.setStatus(HttpServletResponse.SC_OK);
        res.getWriter().write(jsonRespString);
        //res.getWriter().write(jsonResp.toString());
        res.getWriter().flush();
        res.getWriter().close();

        // DONT call supper as it contine the filter chain super.successfulAuthentication(req, res, chain, authResult);
    }


    public String getStrFromInputSteam(InputStream in) {
        BufferedReader bf = null;
        try {
            bf = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //最好在将字节流转换为字符流的时候 进行转码
        StringBuffer buffer = new StringBuffer();
        String line = "";
        try {
            while ((line = bf.readLine()) != null) {
                buffer.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }
}
