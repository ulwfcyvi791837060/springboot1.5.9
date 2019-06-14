package com.yyx.aio.config;


import com.yyx.aio.config.filter.CorsFilter;
import com.yyx.aio.config.filter.GenerateTokenForUserFilter;
import com.yyx.aio.config.filter.VerifyTokenFilter;
import com.yyx.aio.config.handler.EntryPointUnauthorizedHandler;
import com.yyx.aio.config.handler.MyAccessDeniedHandler;
import com.yyx.aio.config.identity.TokenUtil;
import com.yyx.aio.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.ConcurrentSessionFilter;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.security.web.session.SimpleRedirectSessionInformationExpiredStrategy;

import javax.annotation.Resource;


/**
 * <Description> <br>
 *
 * @author yangkai<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年1月13日 <br>
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;
    @Resource
    SessionRegistry sessionRegistry;
    @Autowired
    private TokenUtil tokenUtil;
    /**
     * 注册 401 处理器
     */
    @Autowired
    private EntryPointUnauthorizedHandler unauthorizedHandler;

    /**
     * 注册 403 处理器
     */
    @Autowired
    private MyAccessDeniedHandler accessDeniedHandler;



    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .formLogin().loginPage("/login").usernameParameter("username").passwordParameter("password").successForwardUrl("/users")
                .and()
                .addFilterAt(new ConcurrentSessionFilter(sessionRegistry,sessionInformationExpiredStrategy()),ConcurrentSessionFilter.class)
                .addFilterBefore(new CorsFilter(), ChannelProcessingFilter.class)
                .addFilterBefore(new VerifyTokenFilter(tokenUtil), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new GenerateTokenForUserFilter("/login", authenticationManager(), tokenUtil,sessionRegistry), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/login").permitAll()
                .antMatchers("/loginpage").permitAll()
                .antMatchers("/login.html").permitAll()
                .antMatchers("/logout").permitAll()
                /*静态资源*/
                .antMatchers("/images/**").permitAll()
                .antMatchers("/static/js/**").permitAll()
                .antMatchers("/css/**").permitAll()
                .antMatchers("/fonts/**").permitAll()
                .antMatchers("/favicon.ico").permitAll()
                /*Swagger 开发环境*/
                .antMatchers("/swagger-ui.html").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/v2/**").permitAll()
                /*关闭权限验证*/
                .antMatchers("/**").permitAll()
                .antMatchers("/").permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(this.unauthorizedHandler).accessDeniedHandler(this.accessDeniedHandler)
                .and()
                .logout()
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .and()
                .httpBasic();
        http.csrf().disable();
    }
    /*exceptionHandling().authenticationEntryPoint(this.unauthorizedHandler).accessDeniedHandler(this.accessDeniedHandler)
    .and()
    .anonymous().and()
    .csrf().disable()
    .addFilterBefore(new CorsFilter(), ChannelProcessingFilter.class)
    .addFilterBefore(new VerifyTokenFilter(tokenUtil), UsernamePasswordAuthenticationFilter.class)
    .addFilterBefore(new GenerateTokenForUserFilter("/users", authenticationManager(), tokenUtil), UsernamePasswordAuthenticationFilter.class)
    .authorizeRequests()
    .anyRequest().fullyAuthenticated()
    .and().formLogin().successForwardUrl("/users").and()
    .logout()
    .invalidateHttpSession(true)
    .clearAuthentication(true)
    .and()
    .httpBasic();*/

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        /*DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setHideUserNotFoundExceptions(false);
        provider.setUserDetailsService(userDetailsService);
        provider.setSaltSource(new SaltSource() {
            @Override
            public Object getSalt(UserDetails user) {
                User user1 = (User) user;
                return "aiom";
            }
        });
        provider.setPasswordEncoder(new MyPasswordEncoder());
        *//*provider.setPasswordEncoder(new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return MD5Util.encode((String) rawPassword);
            }

            *//**//**
             * rawPassword 是登录密码
             * encodedPassword 数据库密码
             * *//**//*
            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return encodedPassword.equals(encode(rawPassword));
            }
        });*//*
        return provider;*/
        return null;
    }
    //session失效跳转
    private SessionInformationExpiredStrategy sessionInformationExpiredStrategy() {
        return new SimpleRedirectSessionInformationExpiredStrategy("/login");
    }
    @Bean
    public SessionRegistry getSessionRegistry() {
        SessionRegistry sessionRegistry = new SessionRegistryImpl();
        return sessionRegistry;
    }


}
